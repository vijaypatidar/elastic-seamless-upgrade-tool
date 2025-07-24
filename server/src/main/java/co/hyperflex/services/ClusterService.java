package co.hyperflex.services;

import co.hyperflex.dtos.AddClusterRequest;
import co.hyperflex.dtos.AddClusterResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.mappers.ClusterMapper;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import java.util.List;
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


    final List<ClusterNode> clusterNodes = request.kibanaNodes()
        .stream()
        .map(kibanaNodeRequest -> {
          ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
          node.setCluster(cluster);
          return node;
        }).toList();


    return new AddClusterResponse(cluster.getId());
  }
}
