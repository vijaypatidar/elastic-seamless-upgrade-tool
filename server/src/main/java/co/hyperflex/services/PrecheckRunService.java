package co.hyperflex.services;

import co.hyperflex.dtos.prechecks.GetBreakingChangeEntry;
import co.hyperflex.dtos.prechecks.GetClusterPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponse;
import co.hyperflex.dtos.prechecks.GetIndexPrecheckGroup;
import co.hyperflex.dtos.prechecks.GetNodePrecheckGroup;
import co.hyperflex.dtos.prechecks.GetPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetPrecheckSummaryResponse;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.dtos.prechecks.SkipPrecheckResponse;
import co.hyperflex.entities.precheck.ClusterPrecheckRun;
import co.hyperflex.entities.precheck.IndexPrecheckRun;
import co.hyperflex.entities.precheck.NodePrecheckRun;
import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.mappers.PrecheckMapper;
import co.hyperflex.repositories.BreakingChangeRepository;
import co.hyperflex.repositories.PrecheckRunRepository;
import co.hyperflex.repositories.projection.PrecheckStatusAndSeverityView;
import co.hyperflex.services.notifications.NotificationService;
import co.hyperflex.services.notifications.PrecheckProgressChangeEvent;
import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class PrecheckRunService {

  private final PrecheckRunRepository precheckRunRepository;
  private final BreakingChangeRepository breakingChangeRepository;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final MongoTemplate mongoTemplate;
  private final PrecheckMapper precheckMapper;
  private final NotificationService notificationService;

  public PrecheckRunService(PrecheckRunRepository precheckRunRepository,
                            BreakingChangeRepository breakingChangeRepository, ClusterUpgradeJobService clusterUpgradeJobService,
                            MongoTemplate mongoTemplate, PrecheckMapper precheckMapper, NotificationService notificationService) {
    this.precheckRunRepository = precheckRunRepository;
    this.breakingChangeRepository = breakingChangeRepository;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.mongoTemplate = mongoTemplate;
    this.precheckMapper = precheckMapper;
    this.notificationService = notificationService;
  }

  public GetGroupedPrecheckResponse getGroupedPrecheckByClusterId(String clusterId) {
    List<GetClusterPrecheckEntry> clusterPrechecks = getClusterPrechecks(clusterId);
    List<GetNodePrecheckGroup> nodeGroups = getNodePrecheckGroups(clusterId);
    List<GetIndexPrecheckGroup> indexGroups = getIndexPrecheckGroups(clusterId);
    List<GetBreakingChangeEntry> breakingChangeEntries = getBreakingChanges(clusterId);
    return new GetGroupedPrecheckResponse(nodeGroups, clusterPrechecks, indexGroups, breakingChangeEntries);
  }

  public List<GetBreakingChangeEntry> getBreakingChanges(final String clusterId) {
    var clusterUpgradeJob = clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
    return breakingChangeRepository.getBreakingChanges(clusterUpgradeJob.getCurrentVersion(), clusterUpgradeJob.getTargetVersion())
        .stream()
        .map(breakingChange -> new GetBreakingChangeEntry(
            breakingChange.getId(),
            breakingChange.getTitle() + "(" + breakingChange.getVersion() + ")",
            List.of(
                "Category: " + breakingChange.getCategory(),
                breakingChange.getDescription(),
                breakingChange.getUrl()
            ),
            PrecheckSeverity.WARNING,
            PrecheckStatus.FAILED
        ))
        .toList();
  }

  public List<GetClusterPrecheckEntry> getClusterPrechecks(String clusterId) {
    ClusterUpgradeJob clusterUpgradeJob = clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
    List<PrecheckRun> precheckRuns = precheckRunRepository.getAllByJobId(clusterUpgradeJob.getId(), PrecheckType.CLUSTER);
    return precheckRuns.stream().filter(pr -> pr instanceof ClusterPrecheckRun)
        .map(precheckMapper::toClusterPrecheckEntry).toList();
  }

  public List<GetIndexPrecheckGroup> getIndexPrecheckGroups(String clusterId) {
    ClusterUpgradeJob clusterUpgradeJob = clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
    List<PrecheckRun> precheckRuns = precheckRunRepository.getAllByJobId(clusterUpgradeJob.getId(), PrecheckType.INDEX);
    Map<String, List<GetPrecheckEntry>> indexPrechecks =
        precheckRuns.stream().filter(pr -> pr instanceof IndexPrecheckRun)
            .map(pr -> (IndexPrecheckRun) pr).collect(
                Collectors.groupingBy(pr -> pr.getIndex().getName(),
                    Collectors.mapping(precheckMapper::toPrecheckEntry, Collectors.toList())));
    return indexPrechecks.entrySet().stream().map(entry -> {
      PrecheckStatus status = getMergedPrecheckStatusFromEntries(entry.getValue());
      PrecheckSeverity severity = getMergedPrecheckSeverity(entry.getValue());
      return new GetIndexPrecheckGroup(entry.getKey(), entry.getKey(), status, severity,
          entry.getValue());
    }).toList();
  }

  public List<GetNodePrecheckGroup> getNodePrecheckGroups(String clusterId) {
    ClusterUpgradeJob clusterUpgradeJob = clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
    List<PrecheckRun> precheckRuns = precheckRunRepository.getAllByJobId(clusterUpgradeJob.getId(), PrecheckType.NODE);
    Map<String, List<GetPrecheckEntry>> nodePrechecks =
        precheckRuns.stream().filter(pr -> pr instanceof NodePrecheckRun)
            .map(pr -> (NodePrecheckRun) pr).collect(
                Collectors.groupingBy(pr -> pr.getNode().id(),
                    Collectors.mapping(precheckMapper::toPrecheckEntry, Collectors.toList())));

    return nodePrechecks.entrySet().stream().map(entry -> {

      NodePrecheckRun.NodeInfo nodeInfo = ((NodePrecheckRun) precheckRuns.stream().filter(
              pr -> pr instanceof NodePrecheckRun nodePrecheckRun
                  && nodePrecheckRun.getNode().id().equals(entry.getKey())).findFirst()
          .orElseThrow()).getNode();

      PrecheckStatus status = getMergedPrecheckStatusFromEntries(entry.getValue());
      PrecheckSeverity severity = getMergedPrecheckSeverity(entry.getValue());
      return new GetNodePrecheckGroup(nodeInfo.id(), nodeInfo.ip(), nodeInfo.name(),
          status, severity, entry.getValue(), nodeInfo.rank());
    }).sorted(Comparator.comparingInt(GetNodePrecheckGroup::rank)).toList();
  }

  public PrecheckStatus getStatusByUpgradeJobId(@NotNull String upgradeJobId) {
    List<PrecheckStatus> statuses = precheckRunRepository.findStatusAndSeverityByUpgradeJobId(upgradeJobId)
        .stream()
        .filter(status -> status.severity() == PrecheckSeverity.ERROR)
        .map(PrecheckStatusAndSeverityView::status)
        .toList();
    return getMergedPrecheckStatus(statuses);
  }

  public void rerunPrechecks(String clusterUpgradeJobId, PrecheckRerunRequest request) {
    List<Criteria> criteriaList = new LinkedList<>();

    if (request.precheckIds() != null && !request.precheckIds().isEmpty()) {
      criteriaList.add(Criteria.where("_id").in(request.precheckIds()));
    }

    if (request.nodeIds() != null && !request.nodeIds().isEmpty()) {
      criteriaList.add(Criteria.where("node._id").in(request.nodeIds()));
    }

    if (request.indexNames() != null && !request.indexNames().isEmpty()) {
      criteriaList.add(Criteria.where("index.name").in(request.indexNames()));
    }

    if (Boolean.TRUE.equals(request.cluster())) {
      criteriaList.add(Criteria.where("type").is(PrecheckType.CLUSTER));
    }

    Query query;
    if (criteriaList.isEmpty()) {
      query = new Query(Criteria.where(PrecheckRun.CLUSTER_UPGRADE_JOB_ID).is(clusterUpgradeJobId));
    } else {
      query = new Query(new Criteria().orOperator(criteriaList)
          .andOperator(Criteria.where(PrecheckRun.CLUSTER_UPGRADE_JOB_ID).is(clusterUpgradeJobId)));
    }
    Update update = new Update().set(PrecheckRun.STATUS, PrecheckStatus.PENDING).set(PrecheckRun.START_TIME, null)
        .set(PrecheckRun.END_TIME, null);
    notificationService.sendNotification(new PrecheckProgressChangeEvent());
    mongoTemplate.updateMulti(query, update, PrecheckRun.class);
  }

  private PrecheckStatus getMergedPrecheckStatus(List<PrecheckStatus> statuses) {
    boolean hasCompleted = false;
    boolean hasPending = false;
    boolean hasRunning = false;

    for (var status : statuses) {
      if (status == PrecheckStatus.FAILED) {
        return PrecheckStatus.FAILED;
      }
      if (status == PrecheckStatus.RUNNING) {
        hasRunning = true;
      }
      if (status == PrecheckStatus.PENDING) {
        hasPending = true;
      }
      if (status == PrecheckStatus.COMPLETED) {
        hasCompleted = true;
      }
    }

    if ((hasPending && hasCompleted) || hasRunning) {
      return PrecheckStatus.RUNNING;
    }
    if (hasPending) {
      return PrecheckStatus.PENDING;
    }
    return PrecheckStatus.COMPLETED;
  }

  private PrecheckStatus getMergedPrecheckStatusFromEntries(List<GetPrecheckEntry> prechecks) {
    List<PrecheckStatus> statuses = prechecks
        .stream()
        .map(GetPrecheckEntry::status)
        .toList();
    return this.getMergedPrecheckStatus(statuses);
  }

  private PrecheckSeverity getMergedPrecheckSeverity(List<GetPrecheckEntry> prechecks) {
    Optional<PrecheckSeverity> severityOptional = prechecks.stream()
        .filter(precheck -> precheck.status() == PrecheckStatus.FAILED)
        .map(GetPrecheckEntry::severity)
        .filter(severity -> severity == PrecheckSeverity.ERROR)
        .findAny();
    return severityOptional.orElse(PrecheckSeverity.WARNING);
  }

  public void addLog(String precheckRunId, String message) {
    precheckRunRepository.addLog(precheckRunId, message);
  }

  public List<PrecheckRun> getPendingPrechecks() {
    return precheckRunRepository.getPendingPrechecks();
  }

  public void updatePrecheckStatus(String id, PrecheckStatus precheckStatus) {
    Update update = new Update().set(PrecheckRun.STATUS, precheckStatus);
    if (precheckStatus == PrecheckStatus.RUNNING) {
      update.set(PrecheckRun.START_TIME, new Date());
      update.set(PrecheckRun.LOGS, new LinkedList<>());
    } else if (precheckStatus == PrecheckStatus.PENDING) {
      update.set(PrecheckRun.END_TIME, null);
      update.set(PrecheckRun.START_TIME, null);
    } else if (precheckStatus == PrecheckStatus.COMPLETED || precheckStatus == PrecheckStatus.FAILED) {
      update.set(PrecheckRun.END_TIME, new Date());
    }
    precheckRunRepository.updateById(id, update);
  }

  public boolean precheckExistsForJob(String upgradeJobId) {
    return precheckRunRepository.getCountByJobId(upgradeJobId) > 0;
  }

  public GetPrecheckSummaryResponse getSummary(String clusterId) {
    var job = clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
    var counts = precheckRunRepository.findStatusAndSeverityByUpgradeJobId(job.getId()).stream()
        .filter(p -> p.status() == PrecheckStatus.FAILED)
        .collect(Collectors.groupingBy(
            PrecheckStatusAndSeverityView::severity,
            Collectors.counting()
        ));
    return new GetPrecheckSummaryResponse(
        counts.getOrDefault(PrecheckSeverity.ERROR, 0L),
        counts.getOrDefault(PrecheckSeverity.WARNING, 0L),
        counts.getOrDefault(PrecheckSeverity.SKIPPED, 0L)
    );
  }

  public SkipPrecheckResponse skipPrecheck(String id) {
    Query query = new Query(Criteria.where("_id").in(id));
    Update update = new Update().set(PrecheckRun.SEVERITY, PrecheckSeverity.SKIPPED);
    mongoTemplate.updateMulti(query, update, PrecheckRun.class);
    notificationService.sendNotification(new PrecheckProgressChangeEvent());
    return new SkipPrecheckResponse();
  }
}