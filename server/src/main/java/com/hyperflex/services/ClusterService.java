package com.hyperflex.services;

import com.hyperflex.dtos.AddClusterRequest;
import com.hyperflex.dtos.AddClusterResponse;
import com.hyperflex.entities.cluster.Cluster;
import com.hyperflex.entities.cluster.ClusterNode;
import com.hyperflex.mappers.ClusterMapper;
import com.hyperflex.repositories.ClusterNodeRepository;
import com.hyperflex.repositories.ClusterRepository;
import jakarta.transaction.Transactional;
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

  @Transactional
  public AddClusterResponse add(final AddClusterRequest request) {
    final Cluster cluster = this.clusterMapper.toEntity(request);

    clusterRepository.save(cluster);

    final List<ClusterNode> clusterNodes = request.kibanaNodes()
        .stream()
        .map(kibanaNodeRequest -> {
          ClusterNode node = clusterMapper.toNodeEntity(kibanaNodeRequest);
          node.setCluster(cluster);
          return node;
        }).toList();

    clusterNodeRepository.saveAll(clusterNodes);

    return new AddClusterResponse(cluster.getId());
  }
}
