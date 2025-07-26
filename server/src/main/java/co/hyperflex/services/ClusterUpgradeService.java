package co.hyperflex.services;

import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.clients.KibanaClientProvider;
import co.hyperflex.controllers.UpgradeController;
import co.hyperflex.dtos.ClusterInfoResponse;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeRequest;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeResponse;
import co.hyperflex.dtos.upgrades.ClusterUpgradeResponse;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobRequest;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobResponse;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.ClusterUpgradeStatus;
import co.hyperflex.exceptions.ConflictException;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterUpgradeJobRepository;
import co.hyperflex.upgrader.planner.UpgradePlanBuilder;
import co.hyperflex.upgrader.tasks.Configuration;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClusterUpgradeService {
  private static final Logger log = LoggerFactory.getLogger(ClusterUpgradeService.class);
  private final ClusterUpgradeJobRepository clusterUpgradeJobRepository;
  private final ElasticsearchClientProvider elasticsearchClientProvider;
  private final ClusterNodeRepository clusterNodeRepository;
  private final KibanaClientProvider kibanaClientProvider;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  public ClusterUpgradeService(ClusterUpgradeJobRepository clusterUpgradeJobRepository,
                               ElasticsearchClientProvider elasticsearchClientProvider,
                               ClusterNodeRepository clusterNodeRepository,
                               KibanaClientProvider kibanaClientProvider,
                               ClusterUpgradeJobService clusterUpgradeJobService) {
    this.clusterUpgradeJobRepository = clusterUpgradeJobRepository;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.clusterNodeRepository = clusterNodeRepository;
    this.kibanaClientProvider = kibanaClientProvider;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
  }

  public CreateClusterUpgradeJobResponse createClusterUpgradeJob(
      @NotNull CreateClusterUpgradeJobRequest request) {

    final String clusterId = request.clusterId();

    List<ClusterUpgradeJob> activeJobs =
        clusterUpgradeJobRepository.findByClusterIdAndStatusIsNot(clusterId,
            ClusterUpgradeStatus.UPDATED);

    if (!activeJobs.isEmpty()) {
      log.error("Cluster upgrade job already exists for cluster id {}.", clusterId);
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

    return new CreateClusterUpgradeJobResponse("Cluster upgrade job created successfully",
        clusterUpgradeJob.getId());

  }

  public ClusterNodeUpgradeResponse upgradeNode(ClusterNodeUpgradeRequest request) {

    ClusterUpgradeJob clusterUpgradeJob =
        clusterUpgradeJobService.getActiveClusterJobByClusterId(request.clusterId());
    ClusterNode clusterNode = clusterNodeRepository.findById(request.nodeId()).orElseThrow();
    Logger logger = LoggerFactory.getLogger(UpgradeController.class);

    ElasticClient elasticClient =
        elasticsearchClientProvider.getElasticsearchClientByClusterId(request.clusterId());
    KibanaClient kibanaClient =
        kibanaClientProvider.getKibanaClientByClusterId(request.clusterId());

    Configuration config = new Configuration(9300, 9200, "ubuntu",
        "/Users/vijay/Projects/elastic-seamless-upgrade-tool/backend/ansible/ssh-keys/SSH_key.pem",
        clusterUpgradeJob.getTargetVersion());
    Context context = new Context(clusterNode, config, logger, elasticClient, kibanaClient);


    UpgradePlanBuilder upgradePlanBuilder = new UpgradePlanBuilder();

    List<Task> tasks = upgradePlanBuilder.buildPlanFor(clusterNode);

    executorService.submit(() -> {
      int seq = 0;
      for (Task task : tasks) {

        TaskResult result = task.run(context);
        System.out.println(result);
        if (!result.isSuccess()) {
          logger.error("Task [taskId: {}] [Sequence: {}] failed with result {}", task.getId(), seq,
              result);
          throw new RuntimeException(result.getMessage());
        }
        seq++;
      }
    });

    return new ClusterNodeUpgradeResponse("Node upgrade started");
  }

  public ClusterUpgradeResponse upgrade(String clusterId) {
    return new ClusterUpgradeResponse("Cluster upgrade started");
  }

  public ClusterInfoResponse upgradeInfo(String clusterId) {
    return new ClusterInfoResponse(
        new ClusterInfoResponse.Elastic(
            true,
            new ClusterInfoResponse.Deprecations(1, 2),
            new ClusterInfoResponse.Elastic.SnapshotWrapper(null, null),
            null
        ),
        new ClusterInfoResponse.Kibana(true, new ClusterInfoResponse.Deprecations(1, 1)),
        new ClusterInfoResponse.Precheck(PrecheckStatus.PASSED)
    );
  }
}
