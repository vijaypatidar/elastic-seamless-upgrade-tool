package co.hyperflex.prechecks.scheduler;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.ElasticsearchClientProvider;
import co.hyperflex.clients.elastic.dto.cat.indices.IndicesRecord;
import co.hyperflex.dtos.clusters.GetClusterNodeResponse;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.dtos.prechecks.PrecheckScheduleResponse;
import co.hyperflex.entities.precheck.ClusterPrecheckRunEntity;
import co.hyperflex.entities.precheck.IndexPrecheckRunEntity;
import co.hyperflex.entities.precheck.NodePrecheckRunEntity;
import co.hyperflex.entities.upgrade.ClusterUpgradeJobEntity;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.contexts.PrecheckContext;
import co.hyperflex.prechecks.contexts.resolver.PrecheckContextResolver;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import co.hyperflex.prechecks.core.BaseNodePrecheck;
import co.hyperflex.prechecks.registry.PrecheckRegistry;
import co.hyperflex.repositories.PrecheckRunRepository;
import co.hyperflex.services.ClusterService;
import co.hyperflex.services.ClusterUpgradeJobService;
import co.hyperflex.services.PrecheckRunService;
import co.hyperflex.utils.HashUtil;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PrecheckSchedulerService {
  private final PrecheckRunRepository precheckRunRepository;
  private final ClusterService clusterService;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final PrecheckRegistry precheckRegistry;
  private final ElasticsearchClientProvider elasticsearchClientProvider;
  private final PrecheckRunService precheckRunService;
  private final PrecheckContextResolver precheckContextResolver;

  public PrecheckSchedulerService(PrecheckRunRepository precheckRunRepository,
                                  ClusterService clusterService,
                                  ClusterUpgradeJobService clusterUpgradeJobService,
                                  PrecheckRegistry precheckRegistry,
                                  ElasticsearchClientProvider elasticsearchClientProvider,
                                  PrecheckRunService precheckRunService,
                                  PrecheckContextResolver precheckContextResolver) {
    this.precheckRunRepository = precheckRunRepository;
    this.clusterService = clusterService;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.precheckRegistry = precheckRegistry;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
    this.precheckRunService = precheckRunService;
    this.precheckContextResolver = precheckContextResolver;
  }

  public PrecheckScheduleResponse schedule(String clusterId) {
    final ClusterUpgradeJobEntity clusterUpgradeJob = clusterUpgradeJobService.getActiveJobByClusterId(clusterId);
    final String upgradeJobId = clusterUpgradeJob.getId();
    scheduleNodePrechecks(upgradeJobId, clusterId);
    scheduleClusterPrechecks(upgradeJobId, clusterId);
    scheduleIndexPrechecks(upgradeJobId, clusterId);
    return new PrecheckScheduleResponse(upgradeJobId);
  }

  public void scheduleNodePrechecks(String upgradeJobId, String clusterId) {
    List<GetClusterNodeResponse> nodes = clusterService.getNodes(clusterId);
    precheckRegistry.getNodePrechecks()
        .stream()
        .parallel()
        .flatMap(precheck -> nodes.stream().map(node -> {
          NodePrecheckRunEntity precheckRun = new NodePrecheckRunEntity();
          precheckRun.setId(HashUtil.generateHash(precheck.getId() + ":" + upgradeJobId + ":" + node.id()));
          precheckRun.setPrecheckId(precheck.getId());
          precheckRun.setNode(
              new NodePrecheckRunEntity.NodeInfo(node.id(), node.name(), node.ip(), node.rank())
          );
          precheckRun.setClusterUpgradeJobId(upgradeJobId);
          precheckRun.setSeverity(precheck.getSeverity());
          precheckRun.setClusterId(clusterId);
          precheckRun.setName(precheck.getName());
          return precheckRun;
        }).filter(precheckRun -> {
          PrecheckContext context = precheckContextResolver.resolveContext(precheckRun);
          return ((BaseNodePrecheck) precheck).shouldRun((NodeContext) context);
        }))
        .forEach(precheckRunRepository::save);
  }

  public void scheduleClusterPrechecks(String upgradeJobId, String clusterId) {
    precheckRegistry.getClusterPrechecks().stream().parallel().map(precheck -> {
      ClusterPrecheckRunEntity precheckRun = new ClusterPrecheckRunEntity();
      precheckRun.setId(HashUtil.generateHash(precheck.getId() + ":" + upgradeJobId));
      precheckRun.setPrecheckId(precheck.getId());
      precheckRun.setClusterUpgradeJobId(upgradeJobId);
      precheckRun.setSeverity(precheck.getSeverity());
      precheckRun.setClusterId(clusterId);
      precheckRun.setName(precheck.getName());
      return precheckRun;
    }).forEach(precheckRunRepository::save);
  }

  public void scheduleIndexPrechecks(String upgradeJobId, String clusterId) {
    ElasticClient elasticClient =
        elasticsearchClientProvider.getClientByClusterId(clusterId);
    final List<IndicesRecord> indexes = elasticClient.getIndices();
    precheckRegistry.getIndexPrechecks()
        .stream()
        .parallel()
        .flatMap(precheck -> indexes
            .stream()
            .map(index -> {
              IndexPrecheckRunEntity precheckRun = new IndexPrecheckRunEntity();
              precheckRun.setId(HashUtil.generateHash(precheck.getId() + ":" + upgradeJobId + ":" + index));
              precheckRun.setIndex(new IndexPrecheckRunEntity.IndexInfo(index.getIndex()));
              precheckRun.setPrecheckId(precheck.getId());
              precheckRun.setClusterUpgradeJobId(upgradeJobId);
              precheckRun.setSeverity(precheck.getSeverity());
              precheckRun.setName(precheck.getName());
              precheckRun.setClusterId(clusterId);
              return precheckRun;
            }).filter(precheckRun -> {
              PrecheckContext context = precheckContextResolver.resolveContext(precheckRun);
              return ((BaseIndexPrecheck) precheck).shouldRun((IndexContext) context);
            })).forEach(precheckRunRepository::save);
  }

  public PrecheckScheduleResponse rerunPrechecks(String clusterId,
                                                 PrecheckRerunRequest request) {
    ClusterUpgradeJobEntity upgradeJob = clusterUpgradeJobService.getActiveJobByClusterId(clusterId);
    precheckRunService.rerunPrechecks(upgradeJob.getId(), request);
    return new PrecheckScheduleResponse(upgradeJob.getId());
  }
}
