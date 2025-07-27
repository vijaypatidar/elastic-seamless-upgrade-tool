package co.hyperflex.services;

import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.clients.KibanaClientProvider;
import co.hyperflex.dtos.ClusterInfoResponse;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeRequest;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeResponse;
import co.hyperflex.dtos.upgrades.ClusterUpgradeResponse;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobRequest;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.ClusterUpgradeStatus;
import co.hyperflex.entities.upgrade.NodeUpgradeStatus;
import co.hyperflex.exceptions.BadRequestException;
import co.hyperflex.exceptions.ConflictException;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClusterUpgradeService {
  private static final Logger log = LoggerFactory.getLogger(ClusterUpgradeService.class);
  private final ClusterUpgradeJobRepository clusterUpgradeJobRepository;
  private final ElasticsearchClientProvider elasticsearchClientProvider;
  private final ClusterNodeRepository clusterNodeRepository;
  private final ClusterRepository clusterRepository;
  private final KibanaClientProvider kibanaClientProvider;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private final Lock lock = new ReentrantLock();

  public ClusterUpgradeService(ClusterUpgradeJobRepository clusterUpgradeJobRepository,
                               ElasticsearchClientProvider elasticsearchClientProvider,
                               ClusterNodeRepository clusterNodeRepository,
                               ClusterRepository clusterRepository,
                               KibanaClientProvider kibanaClientProvider,
                               ClusterUpgradeJobService clusterUpgradeJobService) {
    this.clusterUpgradeJobRepository = clusterUpgradeJobRepository;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterRepository = clusterRepository;
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

    Cluster cluster = clusterRepository.findById(request.clusterId()).orElseThrow();
    if (cluster instanceof SelfManagedCluster selfManagedCluster) {
      ClusterUpgradeJob clusterUpgradeJob =
          clusterUpgradeJobService.getActiveClusterJobByClusterId(request.clusterId());
      ClusterNode clusterNode = clusterNodeRepository.findById(request.nodeId()).orElseThrow();
      upgradeNodes(selfManagedCluster, List.of(clusterNode), clusterUpgradeJob);
    } else {
      throw new BadRequestException("Upgrade not supported for cluster");
    }
    return new ClusterNodeUpgradeResponse("Node upgrade started");
  }

  public ClusterUpgradeResponse upgrade(String clusterId) {
    Cluster cluster = clusterRepository.findById(clusterId).orElseThrow();
    if (cluster instanceof SelfManagedCluster selfManagedCluster) {
      ClusterUpgradeJob clusterUpgradeJob =
          clusterUpgradeJobService.getActiveClusterJobByClusterId(clusterId);
      List<ClusterNode> clusterNodes = clusterNodeRepository.findByClusterId(clusterId);
      upgradeNodes(selfManagedCluster, clusterNodes, clusterUpgradeJob);
    } else {
      throw new BadRequestException("Upgrade not supported for cluster");
    }
    return new ClusterUpgradeResponse("Cluster upgrade started");
  }

  public ClusterInfoResponse upgradeInfo(String clusterId) {
    return new ClusterInfoResponse(
        new ClusterInfoResponse.Elastic(true, new ClusterInfoResponse.Deprecations(1, 2),
            new ClusterInfoResponse.Elastic.SnapshotWrapper(null, null), null),
        new ClusterInfoResponse.Kibana(true, new ClusterInfoResponse.Deprecations(1, 1)),
        new ClusterInfoResponse.Precheck(PrecheckStatus.PASSED));
  }


  private void upgradeNodes(SelfManagedCluster cluster, List<ClusterNode> nodes,
                            ClusterUpgradeJob clusterUpgradeJob) {

    executorService.submit(() -> {
      try {
        lock.lock();

        ElasticClient elasticClient = elasticsearchClientProvider.getClient(cluster);
        KibanaClient kibanaClient = kibanaClientProvider.getClient(cluster);

        for (ClusterNode node : nodes) {
          Configuration config = new Configuration(9300, 9200, cluster.getSshInfo().username(),
              "/Users/vijay/Projects/elastic-seamless-upgrade-tool/backend/ansible/ssh-keys/SSH_key.pem",
              clusterUpgradeJob.getTargetVersion());
          Context context = new Context(node, config, log, elasticClient, kibanaClient);

          UpgradePlanBuilder upgradePlanBuilder = new UpgradePlanBuilder();

          List<Task> tasks = upgradePlanBuilder.buildPlanFor(node);
          double seq = 0.0;

          updateNodeProgress(node, 0);

          for (Task task : tasks) {
            TaskResult result = task.run(context);
            System.out.println(result);
            if (!result.isSuccess()) {
              log.error("Task [taskId: {}] [Sequence: {}] failed with result {}", task.getId(),
                  seq, result);
              node.setStatus(NodeUpgradeStatus.FAILED);
              updateNodeProgress(node, (int) ((seq / tasks.size()) * 100));
              throw new RuntimeException(result.getMessage());
            }
            updateNodeProgress(node, (int) ((seq / tasks.size()) * 100));
            Thread.sleep(5000);
            seq++;
          }

          node.setStatus(NodeUpgradeStatus.UPGRADED);
          updateNodeProgress(node, 100);

        }
      } catch (Exception e) {
        log.error("Cluster upgrade failed", e);
      } finally {
        lock.unlock();
      }
    });
  }

  private void updateNodeProgress(ClusterNode node, int progress) {
    node.setProgress(progress);
    clusterNodeRepository.save(node);
  }
}
