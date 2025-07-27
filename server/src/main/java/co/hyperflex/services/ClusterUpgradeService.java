package co.hyperflex.services;

import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.clients.KibanaClientProvider;
import co.hyperflex.dtos.ClusterInfoResponse;
import co.hyperflex.dtos.GetElasticsearchSnapshotResponse;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeRequest;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeResponse;
import co.hyperflex.dtos.upgrades.ClusterUpgradeResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import co.hyperflex.entities.precheck.PrecheckGroup;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.NodeUpgradeStatus;
import co.hyperflex.exceptions.BadRequestException;
import co.hyperflex.prechecks.scheduler.PrecheckSchedulerService;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import co.hyperflex.repositories.PrecheckGroupRepository;
import co.hyperflex.services.notifications.NotificationService;
import co.hyperflex.services.notifications.UpgradeProgressChangeEvent;
import co.hyperflex.upgrader.planner.UpgradePlanBuilder;
import co.hyperflex.upgrader.tasks.Configuration;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
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
  private final ElasticsearchClientProvider elasticsearchClientProvider;
  private final ClusterNodeRepository clusterNodeRepository;
  private final ClusterService clusterService;
  private final ClusterRepository clusterRepository;
  private final KibanaClientProvider kibanaClientProvider;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final PrecheckGroupRepository precheckGroupRepository;
  private final NotificationService notificationService;
  private final PrecheckSchedulerService precheckSchedulerService;
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private final Lock lock = new ReentrantLock();

  public ClusterUpgradeService(ElasticsearchClientProvider elasticsearchClientProvider,
                               ClusterNodeRepository clusterNodeRepository,
                               ClusterService clusterService,
                               ClusterRepository clusterRepository,
                               KibanaClientProvider kibanaClientProvider,
                               ClusterUpgradeJobService clusterUpgradeJobService,
                               PrecheckGroupRepository precheckGroupRepository,
                               NotificationService notificationService,
                               PrecheckSchedulerService precheckSchedulerService) {
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.clusterNodeRepository = clusterNodeRepository;
    this.clusterService = clusterService;
    this.clusterRepository = clusterRepository;
    this.kibanaClientProvider = kibanaClientProvider;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.precheckGroupRepository = precheckGroupRepository;
    this.notificationService = notificationService;
    this.precheckSchedulerService = precheckSchedulerService;
  }

  public ClusterNodeUpgradeResponse upgradeNode(ClusterNodeUpgradeRequest request) {

    Cluster cluster = clusterRepository.findById(request.clusterId()).orElseThrow();
    if (cluster instanceof SelfManagedCluster selfManagedCluster) {
      ClusterUpgradeJob clusterUpgradeJob =
          clusterUpgradeJobService.getActiveJobByClusterId(request.clusterId());
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
          clusterUpgradeJobService.getActiveJobByClusterId(clusterId);
      List<ClusterNode> clusterNodes = clusterNodeRepository.findByClusterId(clusterId);
      upgradeNodes(selfManagedCluster, clusterNodes, clusterUpgradeJob);
    } else {
      throw new BadRequestException("Upgrade not supported for cluster");
    }
    return new ClusterUpgradeResponse("Cluster upgrade started");
  }

  public ClusterInfoResponse upgradeInfo(String clusterId) {

    try {
      ElasticClient client =
          elasticsearchClientProvider.getElasticsearchClientByClusterId(clusterId);
      KibanaClient kibanaClient = kibanaClientProvider.getKibanaClientByClusterId(clusterId);

      PrecheckGroup latestPrecheckGroup =
          precheckGroupRepository.findFirstByClusterIdOrderByCreatedAtDesc(clusterId).orElse(null);
      if (latestPrecheckGroup == null) {
        precheckSchedulerService.schedule(clusterId);
      }

      List<GetElasticsearchSnapshotResponse> snapshots = client.getValidSnapshots();

      // Evaluate upgrade status
      boolean isESUpgraded = clusterService.isNodesUpgraded(clusterId, ClusterNodeType.ELASTIC);
      boolean isKibanaUpgraded = clusterService.isNodesUpgraded(clusterId, ClusterNodeType.ELASTIC);

      ClusterInfoResponse.Elastic elastic =
          new ClusterInfoResponse.Elastic(
              !isESUpgraded,
              new ClusterInfoResponse.Deprecations(1, 2),
              new ClusterInfoResponse.Elastic.SnapshotWrapper(
                  snapshots.isEmpty() ? null : snapshots.getFirst(),
                  kibanaClient.getSnapshotCreationPageUrl()
              )
          );

      ClusterInfoResponse.Kibana kibana =
          new ClusterInfoResponse.Kibana(!isKibanaUpgraded && isESUpgraded,
              new ClusterInfoResponse.Deprecations(1, 1));

      ClusterInfoResponse.Precheck precheck =
          new ClusterInfoResponse.Precheck(latestPrecheckGroup == null ? PrecheckStatus.PENDING :
              latestPrecheckGroup.getStatus());

      return new ClusterInfoResponse(
          elastic,
          kibana,
          precheck);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void upgradeNodes(SelfManagedCluster cluster, List<ClusterNode> nodes,
                            ClusterUpgradeJob clusterUpgradeJob) {

    executorService.submit(() -> {
      try {
        lock.lock();

        ElasticClient elasticClient = elasticsearchClientProvider.getClient(cluster);
        KibanaClient kibanaClient = kibanaClientProvider.getClient(cluster);

        for (ClusterNode node : nodes) {
          if (NodeUpgradeStatus.UPGRADED == node.getStatus()) {
            log.info("Skipping node with [NodeId: {}] as its already updated", node.getId());
            continue;
          }
          Configuration config = new Configuration(
              9300,
              9200,
              cluster.getSshInfo().username(),
              cluster.getSshInfo().keyPath(),
              clusterUpgradeJob.getTargetVersion()
          );
          Context context = new Context(node, config, log, elasticClient, kibanaClient);

          UpgradePlanBuilder upgradePlanBuilder = new UpgradePlanBuilder();

          List<Task> tasks = upgradePlanBuilder.buildPlanFor(node);
          double seq = 0.0;
          node.setStatus(NodeUpgradeStatus.UPGRADING);
          updateNodeProgress(node, 0);

          for (Task task : tasks) {
            try {
              log.info("Task [taskId: {}] [Sequence: {}] [NodeIp: {}] Starting task", task.getId(),
                  seq, node.getIp());
              TaskResult result = task.run(context);
              System.out.println(result);
              log.info("Task [taskId: {}] [Sequence: {}] [NodeIp: {}] [Success: {}]  Result: {}",
                  task.getId(),
                  seq, node.getIp(), result.isSuccess(), result);
              if (!result.isSuccess()) {
                node.setStatus(NodeUpgradeStatus.FAILED);
                updateNodeProgress(node, (int) ((seq / tasks.size()) * 100));
                throw new RuntimeException(result.getMessage());
              }
              seq++;
              updateNodeProgress(node, (int) ((seq / tasks.size()) * 100));
              Thread.sleep(2000);
            } finally {
              notificationService.sendNotification(new UpgradeProgressChangeEvent());
            }
          }

          node.setVersion(config.targetVersion());
          node.setStatus(NodeUpgradeStatus.UPGRADED);
          updateNodeProgress(node, 100);
          notificationService.sendNotification(new UpgradeProgressChangeEvent());

        }
      } catch (Exception e) {
        log.error("[ClusterId: {}] Cluster upgrade failed", cluster.getId(), e);
      } finally {
        notificationService.sendNotification(new UpgradeProgressChangeEvent());
        lock.unlock();
      }
    });
  }

  private void updateNodeProgress(ClusterNode node, int progress) {
    node.setProgress(progress);
    clusterNodeRepository.save(node);
  }
}
