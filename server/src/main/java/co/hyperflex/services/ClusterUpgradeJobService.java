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
        .stream().filter(ClusterUpgradeJob::isActive).findFirst().orElseThrow(
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

    List<ClusterUpgradeJob> jobs = clusterUpgradeJobRepository.findByClusterId(clusterId)
        .stream().filter(job -> !job.getStatus().equals(ClusterUpgradeStatus.UPDATED))
        .toList();

    ElasticClient elasticClient =
        elasticsearchClientProvider.getElasticsearchClientByClusterId(clusterId);
    InfoResponse info = elasticClient.getClusterInfo();
    String currentVersion = info.version().number();

    ClusterUpgradeJob existingJob = jobs.stream().filter(
        job -> job.getCurrentVersion().equals(currentVersion)
            && job.getTargetVersion().equals(request.targetVersion())
    ).findFirst().orElse(null);

    jobs.forEach(job -> {
      if (!job.getStatus().equals(ClusterUpgradeStatus.PENDING)) {
        logger.error("Cluster is under upgrade can't create upgrade job for [cluster: {}].", clusterId);
        throw new ConflictException("Cluster is under upgrade can't create upgrade job for cluster");
      }
      job.setActive(false);
    });

    if (existingJob == null) {
      existingJob = new ClusterUpgradeJob();
      existingJob.setClusterId(clusterId);
      existingJob.setTargetVersion(request.targetVersion());
      existingJob.setCurrentVersion(currentVersion);
      existingJob.setActive(true);
      clusterUpgradeJobRepository.save(existingJob);
    } else {
      existingJob.setActive(true);
    }

    clusterUpgradeJobRepository.saveAll(jobs);

    resetUpgradeStatus(clusterId);

    return new CreateClusterUpgradeJobResponse("Cluster upgrade job created successfully", existingJob.getId());
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
