package co.hyperflex.services;

import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.ElasticsearchClientProvider;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobRequest;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobResponse;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.ClusterUpgradeStatus;
import co.hyperflex.entities.upgrade.NodeUpgradeStatus;
import co.hyperflex.exceptions.ConflictException;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterUpgradeJobRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClusterUpgradeJobService {
  private static final Logger logger = LoggerFactory.getLogger(ClusterUpgradeJobService.class);
  private final ClusterUpgradeJobRepository clusterUpgradeJobRepository;
  private final ElasticsearchClientProvider elasticsearchClientProvider;
  private final ClusterNodeRepository clusterNodeRepository;

  public ClusterUpgradeJobService(ClusterUpgradeJobRepository clusterUpgradeJobRepository,
                                  ElasticsearchClientProvider elasticsearchClientProvider,
                                  ClusterNodeRepository clusterNodeRepository) {
    this.clusterUpgradeJobRepository = clusterUpgradeJobRepository;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.clusterNodeRepository = clusterNodeRepository;
  }

  public @NotNull ClusterUpgradeJob getActiveJobByClusterId(@NotNull String clusterId) {
    return clusterUpgradeJobRepository
        .findByClusterIdAndStatusIsNot(clusterId, ClusterUpgradeStatus.UPDATED)
        .stream().findFirst().orElseThrow(
            () -> new NotFoundException("No active cluster job found for cluster ID: " + clusterId));
  }

  public @NotNull boolean clusterUpgradeJobExists(@NotNull String clusterId) {
    try {
      this.getActiveJobByClusterId(clusterId);
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }

  public CreateClusterUpgradeJobResponse createClusterUpgradeJob(
      @NotNull CreateClusterUpgradeJobRequest request) {

    final String clusterId = request.clusterId();

    List<ClusterUpgradeJob> activeJobs =
        clusterUpgradeJobRepository.findByClusterIdAndStatusIsNot(clusterId,
            ClusterUpgradeStatus.UPDATED);

    if (!activeJobs.isEmpty()) {
      logger.error("Cluster upgrade job already exists for cluster id {}.", clusterId);
      throw new ConflictException("Cluster upgrade job already exists");
    }

    ElasticClient elasticClient =
        elasticsearchClientProvider.getElasticsearchClientByClusterId(clusterId);
    InfoResponse info = elasticClient.getClusterInfo();

    ClusterUpgradeJob clusterUpgradeJob = new ClusterUpgradeJob();
    clusterUpgradeJob.setClusterId(clusterId);
    clusterUpgradeJob.setTargetVersion(request.targetVersion());
    clusterUpgradeJob.setCurrentVersion(info.version().number());

    clusterUpgradeJobRepository.save(clusterUpgradeJob);

    resetUpgradeStatus(clusterId);

    return new CreateClusterUpgradeJobResponse("Cluster upgrade job created successfully",
        clusterUpgradeJob.getId());
  }

  private void resetUpgradeStatus(@NotNull String clusterId) {
    List<ClusterNode> clusterNodes = clusterNodeRepository.findByClusterId(clusterId);
    clusterNodes.forEach(node -> {
      node.setStatus(NodeUpgradeStatus.AVAILABLE);
      node.setProgress(0);
    });
    clusterNodeRepository.saveAll(clusterNodes);
  }

}
