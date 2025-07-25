package co.hyperflex.prechecks.scheduler;

import co.hyperflex.dtos.prechecks.PrecheckScheduleRequest;
import co.hyperflex.dtos.prechecks.PrecheckScheduleResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.precheck.ClusterPrecheckRun;
import co.hyperflex.entities.precheck.IndexPrecheckRun;
import co.hyperflex.entities.precheck.NodePrecheckRun;
import co.hyperflex.entities.precheck.PrecheckGroup;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.prechecks.registry.PrecheckRegistry;
import co.hyperflex.repositories.ClusterNodeRepository;
import co.hyperflex.repositories.ClusterRepository;
import co.hyperflex.repositories.PrecheckRunRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PrecheckSchedulerService {
  private final PrecheckRunRepository precheckRunRepository;
  private final ClusterRepository clusterRepository;
  private final ClusterNodeRepository clusterNodeRepository;
  private final PrecheckRegistry precheckRegistry;

  public PrecheckSchedulerService(PrecheckRunRepository precheckRunRepository,
                                  ClusterRepository clusterRepository,
                                  ClusterNodeRepository clusterNodeRepository,
                                  PrecheckRegistry precheckRegistry) {
    this.precheckRunRepository = precheckRunRepository;
    this.clusterRepository = clusterRepository;
    this.clusterNodeRepository = clusterNodeRepository;
    this.precheckRegistry = precheckRegistry;
  }

  public PrecheckScheduleResponse schedule(PrecheckScheduleRequest request) {
    //todo link upgrade job here
    final Cluster cluster = clusterRepository.findById(request.clusterId())
        .orElseThrow(() -> new NotFoundException("Cluster not found"));

    final PrecheckGroup precheckGroup = new PrecheckGroup();
    final List<ClusterNode> nodes = clusterNodeRepository.findByClusterId(cluster.getId());
    scheduleNodePrechecks(precheckGroup, nodes);
    return new PrecheckScheduleResponse(precheckGroup.getId());
  }

  public void scheduleNodePrechecks(PrecheckGroup precheckGroup, List<ClusterNode> nodes) {

    final List<NodePrecheckRun> precheckRuns = precheckRegistry.getNodePrechecks().stream()
        .flatMap(precheck -> nodes.stream().map(node -> {
          NodePrecheckRun precheckRun = new NodePrecheckRun();
          precheckRun.setPrecheckId(precheck.getId());
          precheckRun.setNode(
              new NodePrecheckRun.NodeInfo(node.getId(), node.getName(), node.getIp()));
          precheckRun.setPrecheckGroupId(precheckGroup.getId());
          precheckRun.setSeverity(precheck.getSeverity());
          return precheckRun;
        })).toList();

    precheckRunRepository.saveAll(precheckRuns);

  }

  public void scheduleClusterPrechecks(PrecheckGroup precheckGroup) {

    final List<ClusterPrecheckRun> precheckRuns =
        precheckRegistry.getClusterPrechecks().stream().map(precheck -> {
          ClusterPrecheckRun precheckRun = new ClusterPrecheckRun();
          precheckRun.setPrecheckId(precheck.getId());
          precheckRun.setPrecheckGroupId(precheckGroup.getId());
          precheckRun.setSeverity(precheck.getSeverity());
          return precheckRun;
        }).toList();

    precheckRunRepository.saveAll(precheckRuns);

  }

  public void scheduleIndexPrechecks(PrecheckGroup precheckGroup, List<String> indexes) {

    final List<IndexPrecheckRun> precheckRuns = precheckRegistry.getClusterPrechecks().stream()
        .flatMap(precheck -> indexes.stream().map(index -> {
          IndexPrecheckRun precheckRun = new IndexPrecheckRun();
          precheckRun.setIndex(new IndexPrecheckRun.IndexInfo(index));
          precheckRun.setPrecheckId(precheck.getId());
          precheckRun.setPrecheckGroupId(precheckGroup.getId());
          precheckRun.setSeverity(precheck.getSeverity());
          return precheckRun;
        })).toList();

    precheckRunRepository.saveAll(precheckRuns);

  }

}
