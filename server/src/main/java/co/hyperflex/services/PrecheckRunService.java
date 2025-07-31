package co.hyperflex.services;

import co.hyperflex.dtos.prechecks.GetBreakingChangeEntry;
import co.hyperflex.dtos.prechecks.GetClusterPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponse;
import co.hyperflex.dtos.prechecks.GetIndexPrecheckGroup;
import co.hyperflex.dtos.prechecks.GetNodePrecheckGroup;
import co.hyperflex.dtos.prechecks.GetPrecheckEntry;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.entities.precheck.ClusterPrecheckRun;
import co.hyperflex.entities.precheck.IndexPrecheckRun;
import co.hyperflex.entities.precheck.NodePrecheckRun;
import co.hyperflex.entities.precheck.PrecheckGroup;
import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.mappers.PrecheckMapper;
import co.hyperflex.repositories.BreakingChangeRepository;
import co.hyperflex.repositories.PrecheckGroupRepository;
import co.hyperflex.repositories.PrecheckRunRepository;
import co.hyperflex.repositories.projection.PrecheckStatusAndSeverityView;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class PrecheckRunService {

  private final PrecheckRunRepository precheckRunRepository;
  private final PrecheckGroupRepository precheckGroupRepository;
  private final BreakingChangeRepository breakingChangeRepository;
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final MongoTemplate mongoTemplate;
  private final PrecheckMapper precheckMapper;

  public PrecheckRunService(PrecheckRunRepository precheckRunRepository,
                            PrecheckGroupRepository precheckGroupRepository, BreakingChangeRepository breakingChangeRepository,
                            ClusterUpgradeJobService clusterUpgradeJobService,
                            MongoTemplate mongoTemplate, PrecheckMapper precheckMapper) {
    this.precheckRunRepository = precheckRunRepository;
    this.precheckGroupRepository = precheckGroupRepository;
    this.breakingChangeRepository = breakingChangeRepository;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.mongoTemplate = mongoTemplate;
    this.precheckMapper = precheckMapper;
  }

  public GetGroupedPrecheckResponse getGroupedPrecheckByClusterId(String clusterId) {
    ClusterUpgradeJob clusterUpgradeJob = clusterUpgradeJobService.getActiveJobByClusterId(clusterId);
    return precheckGroupRepository.findFirstByClusterIdOrderByCreatedAtDesc(clusterId)
        .map(precheckGroup -> {
          List<PrecheckRun> precheckRuns =
              precheckRunRepository.findByPrecheckGroupId(precheckGroup.getId());

          Map<String, List<GetPrecheckEntry>> nodePrechecks =
              precheckRuns.stream().filter(pr -> pr instanceof NodePrecheckRun)
                  .map(pr -> (NodePrecheckRun) pr).collect(
                      Collectors.groupingBy(pr -> pr.getNode().getId(),
                          Collectors.mapping(precheckMapper::toPrecheckEntry, Collectors.toList())));

          Map<String, List<GetPrecheckEntry>> indexPrechecks =
              precheckRuns.stream().filter(pr -> pr instanceof IndexPrecheckRun)
                  .map(pr -> (IndexPrecheckRun) pr).collect(
                      Collectors.groupingBy(pr -> pr.getIndex().getName(),
                          Collectors.mapping(precheckMapper::toPrecheckEntry, Collectors.toList())));

          List<GetClusterPrecheckEntry> clusterPrechecks =
              precheckRuns.stream().filter(pr -> pr instanceof ClusterPrecheckRun)
                  .map(precheckMapper::toClusterPrecheckEntry).toList();

          List<GetNodePrecheckGroup> nodeGroups = nodePrechecks.entrySet().stream().map(entry -> {
            NodePrecheckRun.NodeInfo nodeInfo = ((NodePrecheckRun) precheckRuns.stream().filter(
                    pr -> pr instanceof NodePrecheckRun nodePrecheckRun
                        && nodePrecheckRun.getNode().getId().equals(entry.getKey())).findFirst()
                .orElseThrow()).getNode();

            List<PrecheckStatus> statuses = entry.getValue().stream().map(GetPrecheckEntry::status).toList();
            PrecheckStatus status = getMergedPrecheckStatus(statuses);
            return new GetNodePrecheckGroup(nodeInfo.getId(), nodeInfo.getIp(), nodeInfo.getName(),
                status, entry.getValue());
          }).toList();

          List<GetIndexPrecheckGroup> indexGroups =
              indexPrechecks.entrySet().stream().map(entry -> {
                List<PrecheckStatus> statuses = entry.getValue().stream().map(GetPrecheckEntry::status).toList();
                PrecheckStatus status = getMergedPrecheckStatus(statuses);
                return new GetIndexPrecheckGroup(entry.getKey(), entry.getKey(), status,
                    entry.getValue());
              }).toList();

          List<GetBreakingChangeEntry> breakingChangeEntries =
              breakingChangeRepository.getBreakingChanges(clusterUpgradeJob.getCurrentVersion(), clusterUpgradeJob.getTargetVersion())
                  .stream()
                  .map(breakingChange -> new GetBreakingChangeEntry(
                      breakingChange.getId(),
                      breakingChange.getTitle() + "(" + breakingChange.getVersion() + ")",
                      List.of(
                          "Category: " + breakingChange.getCategory(),
                          breakingChange.getDescription(),
                          breakingChange.getUrl()
                      ),
                      PrecheckStatus.FAILED
                  ))
                  .toList();
          return new GetGroupedPrecheckResponse(nodeGroups, clusterPrechecks, indexGroups, breakingChangeEntries);
        })
        .orElseThrow(() -> new NotFoundException("No PrecheckRun found for cluster: " + clusterId));
  }

  public PrecheckStatus getGroupStatus(@NotNull String groupId) {
    List<PrecheckStatus> statuses = precheckRunRepository.findStatusAndSeverityByPrecheckGroupId(groupId)
        .stream()
        .filter(status -> status.severity() == PrecheckSeverity.ERROR)
        .map(PrecheckStatusAndSeverityView::status)
        .toList();
    return getMergedPrecheckStatus(statuses);
  }

  public void rerunPrechecks(PrecheckGroup precheckGroup, PrecheckRerunRequest request) {
    List<Criteria> criteriaList = new LinkedList<>();

    if (request.precheckIds() != null && !request.precheckIds().isEmpty()) {
      criteriaList.add(Criteria.where("_id").in(request.precheckIds()));
    }

    if (request.nodeIds() != null && !request.nodeIds().isEmpty()) {
      criteriaList.add(Criteria.where("node.id").in(request.nodeIds()));
    }

    if (request.indexNames() != null && !request.indexNames().isEmpty()) {
      criteriaList.add(Criteria.where("index.name").in(request.indexNames()));
    }

    if (criteriaList.isEmpty()) {
      criteriaList.add(Criteria.where("type").is(PrecheckType.CLUSTER));
    }

    Query query = new Query(new Criteria().orOperator(criteriaList)
        .andOperator(Criteria.where("precheckGroupId").is(precheckGroup.getId())));
    Update update = new Update().set("status", PrecheckStatus.PENDING).set("startedAt", null)
        .set("endAt", null);
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
}