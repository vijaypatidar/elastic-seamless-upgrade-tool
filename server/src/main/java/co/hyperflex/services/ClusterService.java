package co.hyperflex.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.cat.health.HealthRecord;
import co.elastic.clients.elasticsearch.cat.master.MasterRecord;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.nodes.NodesInfoRequest;
import co.elastic.clients.elasticsearch.nodes.NodesInfoResponse;
import co.elastic.clients.elasticsearch.nodes.info.NodeInfo;
import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.dtos.GetDeprecationsResponse;
import co.hyperflex.dtos.clusters.AddClusterRequest;
import co.hyperflex.dtos.clusters.AddClusterResponse;
import co.hyperflex.dtos.clusters.AddSelfManagedClusterRequest;
import co.hyperflex.dtos.clusters.ClusterOverviewResponse;
import co.hyperflex.dtos.clusters.GetClusterKibanaNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterResponse;
import co.hyperflex.dtos.clusters.UpdateClusterRequest;
import co.hyperflex.dtos.clusters.UpdateClusterResponse;
import co.hyperflex.dtos.clusters.UpdateElasticCloudClusterRequest;
import co.hyperflex.dtos.clusters.UpdateSelfManagedClusterRequest;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.cluster.ElasticCloudCluster;
import co.hyperflex.entities.cluster.ElasticNode;
import co.hyperflex.entities.cluster.OperatingSystemInfo;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import co.hyperflex.entities.cluster.SshInfo;
import co.hyperflex.entities.upgrade.NodeUpgradeStatus;
import co.hyperflex.exceptions.BadRequestException;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.mappers.ClusterMapper;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import co.hyperflex.utils.HashUtil;
import co.hyperflex.utils.NodeRoleRankerUtils;
import co.hyperflex.utils.UpgradePathUtils;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final SshKeyService sshKeyService;

  public ClusterService(ClusterRepository clusterRepository,
                        ClusterNodeRepository clusterNodeRepository, ClusterMapper clusterMapper,
                        ElasticsearchClientProvider elasticsearchClientProvider,
                        ClusterUpgradeJobService clusterUpgradeJobService,
                        SshKeyService sshKeyService) {
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterMapper = clusterMapper;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.sshKeyService = sshKeyService;
  }

  public AddClusterResponse add(final AddClusterRequest request) {
    final Cluster cluster = this.clusterMapper.toEntity(request);
    clusterRepository.save(cluster);
    syncElasticNodes(cluster);
    if (request instanceof AddSelfManagedClusterRequest selfManagedRequest) {
      final List<ClusterNode> clusterNodes = selfManagedRequest.getKibanaNodes()
          .stream()
          .map(kibanaNodeRequest -> {
            ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
            node.setId(HashUtil.generateHash(cluster.getId() + ":" + node.getIp()));
            node.setClusterId(cluster.getId());
            return node;
          }).toList();

      clusterNodeRepository.saveAll(clusterNodes);
    }

    return new AddClusterResponse(cluster.getId());
  }

  public UpdateClusterResponse updateCluster(String clusterId,
                                             UpdateClusterRequest request) {
    Cluster cluster = clusterRepository.findById(clusterId)
        .orElseThrow(() -> new co.hyperflex.exceptions.NotFoundException(
            "Cluster not found with id: " + clusterId));

    cluster.setName(request.getName());
    cluster.setElasticUrl(request.getElasticUrl());
    cluster.setKibanaUrl(request.getKibanaUrl());
    cluster.setUsername(request.getUsername());
    cluster.setApiKey(request.getApiKey());
    cluster.setPassword(request.getPassword());


    if (request instanceof UpdateSelfManagedClusterRequest selfManagedRequest
        && cluster instanceof SelfManagedCluster selfManagedCluster) {
      String file =
          sshKeyService.createSSHPrivateKeyFile(selfManagedRequest.getSshKey(), "SSH_key.pem");
      selfManagedCluster.setSshInfo(
          new SshInfo(selfManagedRequest.getSshUsername(), selfManagedRequest.getSshKey(), file));

      if (selfManagedRequest.getKibanaNodes() != null
          && !selfManagedRequest.getKibanaNodes().isEmpty()) {
        final List<ClusterNode> clusterNodes = selfManagedRequest.getKibanaNodes()
            .stream()
            .map(kibanaNodeRequest -> {
              ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
              node.setClusterId(cluster.getId());
              node.setId(HashUtil.generateHash(cluster.getId() + ":" + node.getIp()));
              return node;
            }).toList();
        clusterNodeRepository.saveAll(clusterNodes);
      }

    } else if (request instanceof UpdateElasticCloudClusterRequest elasticCloudRequest
        && cluster instanceof ElasticCloudCluster elasticCloudCluster) {
      elasticCloudCluster.setDeploymentId(elasticCloudRequest.getDeploymentId());
    } else {
      throw new BadRequestException("Invalid request");
    }

    clusterRepository.save(cluster);
    syncElasticNodes(cluster);
    return new UpdateClusterResponse();
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
    throw new NotFoundException("Cluster not found with id: " + clusterId);
  }

  public List<GetClusterNodeResponse> getNodes(String clusterId, ClusterNodeType type) {
    List<ClusterNode> clusterNodes;

    if (type == null) {
      clusterNodes = clusterNodeRepository.findByClusterId(clusterId);
    } else {
      clusterNodes = clusterNodeRepository.findByClusterIdAndType(clusterId, type);
    }

    int minNonUpgradedNodeRank = clusterNodes.stream()
        .filter(node -> node.getStatus() != NodeUpgradeStatus.UPGRADED)
        .mapToInt(ClusterNode::getRank)
        .min()
        .orElse(Integer.MAX_VALUE);

    boolean isUpgrading =
        clusterNodes.stream().anyMatch(node -> node.getStatus() == NodeUpgradeStatus.UPGRADING);

    return clusterNodes.stream()
        .peek(node ->
            node.setUpgradable(
                node.getStatus() != NodeUpgradeStatus.UPGRADED
                    && !isUpgrading
                    && node.getRank() <= minNonUpgradedNodeRank)
        )
        .sorted(Comparator.comparingInt(ClusterNode::getRank))
        .map(clusterMapper::toGetClusterNodeResponse).toList();
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

  public ClusterOverviewResponse getClusterOverview(String clusterId) {
    Cluster cluster = clusterRepository.getCluster(clusterId);
    ElasticClient elasticClient = elasticsearchClientProvider.getClient(cluster);
    ElasticsearchClient client = elasticClient.getElasticsearchClient();
    boolean upgradeJobExists = clusterUpgradeJobService.clusterUpgradeJobExists(clusterId);
    try {
      InfoResponse info = client.info();
      HealthRecord health = client.cat().health().valueBody().getFirst();
      IndicesResponse indices = client.cat().indices();
      List<MasterRecord> activeMasters = elasticClient.getActiveMasters();
      Boolean adaptiveReplicaEnabled = elasticClient.isAdaptiveReplicaEnabled();
      var counts = elasticClient.getEntitiesCounts();
      return new ClusterOverviewResponse(
          info.clusterName(),
          info.clusterUuid(),
          health.status(),
          info.version().number(),
          false,
          counts.dataNodes(),
          counts.totalNodes(),
          activeMasters.size(),
          activeMasters.stream().map(MasterRecord::id).collect(Collectors.joining(",")),
          adaptiveReplicaEnabled,
          indices.valueBody().size(),
          counts.activePrimaryShards(),
          counts.activeShards(),
          counts.unassignedShards(),
          counts.initializingShards(),
          counts.relocatingShards(),
          cluster.getType().name(),
          info.version().number(),
          UpgradePathUtils.getPossibleUpgrades(info.version().number()),
          upgradeJobExists
      );

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void syncElasticNodes(Cluster cluster) {
    try {
      ElasticClient elasticClient = elasticsearchClientProvider.getClient(cluster);
      ElasticsearchClient client = elasticClient.getElasticsearchClient();
      NodesInfoRequest request = new NodesInfoRequest.Builder().build();
      NodesInfoResponse response = client.nodes().info(request);
      Map<String, NodeInfo> nodes = response.nodes();
      List<ClusterNode> clusterNodes = new LinkedList<>();

      List<MasterRecord> activeMasters = elasticClient.getActiveMasters();

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
        boolean isActiveMaster =
            activeMasters.stream().anyMatch(masterNode -> nodeId.equals(masterNode.id()));
        node.setMaster(isActiveMaster);
        node.setStatus(NodeUpgradeStatus.AVAILABLE);
        node.setType(ClusterNodeType.ELASTIC);
        node.setRank(NodeRoleRankerUtils.getNodeRankByRoles(node.getRoles(), isActiveMaster));

        // Sync with DB
        Optional<ClusterNode> existingNodeOpt = clusterNodeRepository.findById(nodeId);
        existingNodeOpt.ifPresent(existing -> {
          node.setStatus(existing.getStatus());
          node.setProgress(existing.getProgress());
        });

        clusterNodes.add(node);
      }


      clusterNodeRepository.saveAll(clusterNodes);

    } catch (ElasticsearchException e) {
      throw new BadRequestException("Invalid credential");
    } catch (Exception e) {
      log.error("Error syncing nodes from Elasticsearch:", e);
      throw new RuntimeException(e);
    }
  }

  public boolean isNodesUpgraded(String clusterId, ClusterNodeType clusterNodeType) {
    return getNodes(clusterId, clusterNodeType).stream().map(GetClusterNodeResponse::status)
        .map(status -> NodeUpgradeStatus.UPGRADED == status)
        .reduce(true, Boolean::equals);
  }

  public List<GetDeprecationsResponse> getKibanaDeprecations(String clusterId) {
    return getElasticDeprecations(clusterId);
  }

  public List<GetDeprecationsResponse> getElasticDeprecations(String clusterId) {
    return List.of(new GetDeprecationsResponse(
        "The \"xpack.reporting.roles\" setting is deprecated",
        "The default mechanism for Reporting privileges will work differently in future versions.",
        "warning",
        List.of("Set \"xpack.reporting.roles.enabled\" to \"false\" in kibana.yml.")
    ));
  }
}