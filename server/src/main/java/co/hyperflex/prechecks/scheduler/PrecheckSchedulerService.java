package co.hyperflex.prechecks.scheduler;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.ElasticsearchClientProvider;
import co.hyperflex.dtos.clusters.GetClusterNodeResponse;
import co.hyperflex.dtos.prechecks.CreatePrecheckGroupRequest;
import co.hyperflex.dtos.prechecks.CreatePrecheckGroupResponse;
import co.hyperflex.dtos.prechecks.GetPrecheckGroupResponse;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.dtos.prechecks.PrecheckScheduleResponse;
import co.hyperflex.entities.precheck.ClusterPrecheckRun;
import co.hyperflex.entities.precheck.IndexPrecheckRun;
import co.hyperflex.entities.precheck.NodePrecheckRun;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
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
    final ClusterUpgradeJob clusterUpgradeJob = clusterUpgradeJobService.getActiveJobByClusterId(clusterId);
    final CreatePrecheckGroupResponse createPrecheckGroupResponse = precheckRunService.createPrecheckGroup(
        new CreatePrecheckGroupRequest(
            clusterUpgradeJob.getId(),
            clusterId
        )
    );
    final String precheckGroupId = createPrecheckGroupResponse.id();
    scheduleNodePrechecks(precheckGroupId, clusterId);
    scheduleClusterPrechecks(precheckGroupId, clusterId);
    scheduleIndexPrechecks(precheckGroupId, clusterId);

    return new PrecheckScheduleResponse(precheckGroupId);
  }

  public void scheduleNodePrechecks(String precheckGroupId, String clusterId) {
    List<GetClusterNodeResponse> nodes = clusterService.getNodes(clusterId);
    final List<NodePrecheckRun> precheckRuns =
        precheckRegistry.getNodePrechecks()
            .stream()
            .parallel()
            .flatMap(precheck -> nodes.stream().map(node -> {
              NodePrecheckRun precheckRun = new NodePrecheckRun();
              precheckRun.setPrecheckId(precheck.getId());
              precheckRun.setNode(
                  new NodePrecheckRun.NodeInfo(node.id(), node.id(), node.ip())
              );
              precheckRun.setPrecheckGroupId(precheckGroupId);
              precheckRun.setSeverity(precheck.getSeverity());
              precheckRun.setClusterId(clusterId);
              precheckRun.setName(precheck.getName());
              return precheckRun;
            }).filter(precheckRun -> {
              PrecheckContext context = precheckContextResolver.resolveContext(precheckRun);
              return ((BaseNodePrecheck) precheck).shouldRun((NodeContext) context);
            }))
            .toList();

    precheckRunRepository.saveAll(precheckRuns);

  }

  public void scheduleClusterPrechecks(String precheckGroupId, String clusterId) {
    final List<ClusterPrecheckRun> precheckRuns =
        precheckRegistry.getClusterPrechecks().stream().parallel().map(precheck -> {
          ClusterPrecheckRun precheckRun = new ClusterPrecheckRun();
          precheckRun.setPrecheckId(precheck.getId());
          precheckRun.setPrecheckGroupId(precheckGroupId);
          precheckRun.setSeverity(precheck.getSeverity());
          precheckRun.setClusterId(clusterId);
          precheckRun.setName(precheck.getName());
          return precheckRun;
        }).toList();

    precheckRunRepository.saveAll(precheckRuns);

  }

  public void scheduleIndexPrechecks(String precheckGroupId, String clusterId) {
    ElasticClient elasticClient =
        elasticsearchClientProvider.getElasticsearchClientByClusterId(clusterId);
    final List<String> indexes = elasticClient.getIndices();
    final List<IndexPrecheckRun> precheckRuns =
        precheckRegistry.getIndexPrechecks()
            .stream()
            .parallel()
            .flatMap(precheck -> indexes
                .stream()
                .map(index -> {
                  IndexPrecheckRun precheckRun = new IndexPrecheckRun();
                  precheckRun.setIndex(new IndexPrecheckRun.IndexInfo(index));
                  precheckRun.setPrecheckId(precheck.getId());
                  precheckRun.setPrecheckGroupId(precheckGroupId);
                  precheckRun.setSeverity(precheck.getSeverity());
                  precheckRun.setName(precheck.getName());
                  precheckRun.setClusterId(clusterId);
                  return precheckRun;
                }).filter(precheckRun -> {
                  PrecheckContext context = precheckContextResolver.resolveContext(precheckRun);
                  return ((BaseIndexPrecheck) precheck).shouldRun((IndexContext) context);
                })).toList();
    precheckRunRepository.saveAll(precheckRuns);
  }

  public PrecheckScheduleResponse rerunPrechecks(String clusterId,
                                                 PrecheckRerunRequest request) {
    GetPrecheckGroupResponse precheckGroup = precheckRunService.getPrecheckGroupByClusterId(clusterId);
    precheckRunService.rerunPrechecks(precheckGroup.id(), request);
    return new PrecheckScheduleResponse(precheckGroup.id());
  }
}
