package co.hyperflex.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.NodesInfoRequest;
import co.elastic.clients.elasticsearch.nodes.NodesInfoResponse;
import co.elastic.clients.elasticsearch.nodes.info.NodeInfo;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.dtos.clusters.AddClusterRequest;
import co.hyperflex.dtos.clusters.AddClusterResponse;
import co.hyperflex.dtos.clusters.AddSelfManagedClusterRequest;
import co.hyperflex.dtos.clusters.GetClusterKibanaNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterResponse;
import co.hyperflex.dtos.clusters.UpdateClusterRequest;
import co.hyperflex.dtos.clusters.UpdateClusterResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.cluster.ElasticNode;
import co.hyperflex.entities.cluster.OperatingSystemInfo;
import co.hyperflex.entities.upgrade.NodeUpgradeStatus;
import co.hyperflex.mappers.ClusterMapper;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import co.hyperflex.utils.NodeRoleRankerUtils;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClusterService {
  private static final Logger log = LoggerFactory.getLogger(ClusterService.class);
  private final ClusterRepository clusterRepository;
  private final ClusterNodeRepository clusterNodeRepository;
  private final ClusterMapper clusterMapper;
  private final ElasticsearchClientProvider elasticsearchClientProvider;

  public ClusterService(ClusterRepository clusterRepository,
                        ClusterNodeRepository clusterNodeRepository, ClusterMapper clusterMapper,
                        ElasticsearchClientProvider elasticsearchClientProvider) {
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterMapper = clusterMapper;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
  }

  public AddClusterResponse add(final AddClusterRequest request) {
    final Cluster cluster = this.clusterMapper.toEntity(request);
    clusterRepository.save(cluster);
    List<ClusterNode> nodes = buildClusterNodes(cluster);
    clusterNodeRepository.saveAll(nodes);
    if (request instanceof AddSelfManagedClusterRequest selfManagedRequest) {
      final List<ClusterNode> clusterNodes = selfManagedRequest.getKibanaNodes()
          .stream()
          .map(kibanaNodeRequest -> {
            ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
            node.setClusterId(cluster.getId());
            return node;
          }).toList();

      clusterNodeRepository.saveAll(clusterNodes);
    }

    return new AddClusterResponse(cluster.getId());
  }

  public GetClusterResponse getClusterById(String clusterId) {
    Optional<Cluster> optionalCluster = clusterRepository.findById(clusterId);
    if (optionalCluster.isPresent()) {
      Cluster cluster = optionalCluster.get();
      List<ClusterNode> nodes = clusterNodeRepository.findByClusterId(clusterId);
      List<GetClusterKibanaNodeResponse> kibanaNodes = nodes.stream()
          .filter(node -> node.getType() == ClusterNodeType.KIBANA)
          .map(node -> new GetClusterKibanaNodeResponse(node.getId(), node.getName(), node.getIp()))
          .toList();
      return clusterMapper.toGetClusterResponse(cluster, kibanaNodes);
    }
    return null;
  }

  public List<GetClusterNodeResponse> getNodes(String clusterId, ClusterNodeType type) {
    if (type == null) {
      return clusterNodeRepository.findByClusterId(clusterId).stream()
          .map(clusterMapper::toGetClusterNodeResponse).toList();
    }
    return clusterNodeRepository.findByClusterIdAndType(clusterId, type).stream()
        .map(clusterMapper::toGetClusterNodeResponse).toList();
  }

  public UpdateClusterResponse updateCluster(String clusterId,
                                             @Valid UpdateClusterRequest request) {
    return null;
  }

  public List<GetClusterResponse> getClusters() {
    return clusterRepository.findAll().stream()
        .map(cluster -> {
          List<ClusterNode> nodes = clusterNodeRepository.findByClusterId(cluster.getId());
          List<GetClusterKibanaNodeResponse> kibanaNodes = nodes.stream()
              .filter(node -> node.getType() == ClusterNodeType.KIBANA)
              .map(node -> new GetClusterKibanaNodeResponse(node.getId(), node.getName(),
                  node.getIp()))
              .toList();
          return clusterMapper.toGetClusterResponse(cluster, kibanaNodes);
        })
        .toList();
  }

  private List<ClusterNode> buildClusterNodes(Cluster cluster) {

    try {
      ElasticsearchClient client =
          elasticsearchClientProvider.getClient(cluster).getElasticsearchClient();
      NodesInfoRequest request = new NodesInfoRequest.Builder().build();
      NodesInfoResponse response = client.nodes().info(request);
      Map<String, NodeInfo> nodes = response.nodes();
      List<ClusterNode> clusterNodes = new LinkedList<>();
      for (Map.Entry<String, NodeInfo> entry : nodes.entrySet()) {
        String nodeId = entry.getKey();
        NodeInfo value = entry.getValue();

        ElasticNode node = new ElasticNode();
        node.setId(nodeId);
        node.setClusterId(cluster.getId());
        node.setIp(value.ip());
        node.setName(value.name());
        node.setVersion(value.version());
        node.setRoles(
            value.roles().stream().map(Enum::name).toList()
        );

        // Extract OS info
        if (value.os() != null) {
          node.setOs(new OperatingSystemInfo(
              value.os().name(),
              value.os().version(),
              value.os().arch()
          ));
        }

        node.setProgress(0);
        boolean isMaster = false;
        node.setMaster(isMaster);
        node.setStatus(NodeUpgradeStatus.AVAILABLE);
        node.setType(ClusterNodeType.ELASTIC);
        node.setRank(NodeRoleRankerUtils.getNodeRankByRoles(node.getRoles(), isMaster));

        // 5. Sync with DB
        Optional<ClusterNode> existingNodeOpt = clusterNodeRepository.findById(nodeId);
        existingNodeOpt.ifPresent(existing -> {
          node.setStatus(existing.getStatus());
          node.setProgress(existing.getProgress());
        });

        clusterNodes.add(node);
      }

      return clusterNodes;

    } catch (IOException e) {
      log.error("Error syncing nodes from Elasticsearch:", e);
      throw new RuntimeException(e);
    }
  }

}
