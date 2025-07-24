package co.hyperflex.services;

import co.hyperflex.dtos.AddClusterRequest;
import co.hyperflex.dtos.AddClusterResponse;
import co.hyperflex.dtos.GetClusterNodeResponse;
import co.hyperflex.dtos.GetClusterResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.mappers.ClusterMapper;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClusterService {
  private final ClusterRepository clusterRepository;
  private final ClusterNodeRepository clusterNodeRepository;
  private final ClusterMapper clusterMapper;

  public ClusterService(ClusterRepository clusterRepository,
                        ClusterNodeRepository clusterNodeRepository, ClusterMapper clusterMapper) {
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterMapper = clusterMapper;
  }

  public AddClusterResponse add(final AddClusterRequest request) {
    final Cluster cluster = this.clusterMapper.toEntity(request);

    clusterRepository.save(cluster);

    final List<ClusterNode> clusterNodes = request.kibanaNodes()
        .stream()
        .map(kibanaNodeRequest -> {
          ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
          node.setClusterId(cluster.getId());
          return node;
        }).toList();

    clusterNodeRepository.saveAll(clusterNodes);

    return new AddClusterResponse(cluster.getId());
  }

  public GetClusterResponse getClusterById(String clusterId) {
    Optional<Cluster> optionalCluster = clusterRepository.findById(clusterId);
    if (optionalCluster.isPresent()) {
      Cluster cluster = optionalCluster.get();
      List<ClusterNode> nodes = clusterNodeRepository.findByClusterId(clusterId);
      return new GetClusterResponse(
          clusterId,
          cluster.getName(),
          cluster.getElasticUrl(),
          cluster.getKibanaUrl(),
          cluster.getUsername(),
          null
      );
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
}
