package co.hyperflex.controllers;

import co.hyperflex.dtos.prechecks.GetBreakingChangeEntry;
import co.hyperflex.dtos.prechecks.GetClusterPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponse;
import co.hyperflex.dtos.prechecks.GetIndexPrecheckGroup;
import co.hyperflex.dtos.prechecks.GetNodePrecheckGroup;
import co.hyperflex.dtos.prechecks.GetPrecheckSummaryResponse;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.dtos.prechecks.PrecheckScheduleResponse;
import co.hyperflex.dtos.prechecks.SkipPrecheckResponse;
import co.hyperflex.prechecks.scheduler.PrecheckSchedulerService;
import co.hyperflex.services.PrecheckReportService;
import co.hyperflex.services.PrecheckRunService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clusters/{clusterId}/prechecks")
public class PrecheckController {

  private final PrecheckSchedulerService scheduler;
  private final PrecheckRunService precheckRunService;
  private final PrecheckReportService precheckReportService;

  public PrecheckController(PrecheckSchedulerService scheduler,
                            PrecheckRunService precheckRunService,
                            PrecheckReportService precheckReportService) {
    this.scheduler = scheduler;
    this.precheckRunService = precheckRunService;
    this.precheckReportService = precheckReportService;
  }

  @PostMapping()
  public PrecheckScheduleResponse runAll(@PathVariable String clusterId) {
    return scheduler.schedule(clusterId);
  }

  @PutMapping("/{id}/skip")
  public SkipPrecheckResponse skip(@PathVariable String clusterId, @PathVariable String id) {
    return precheckRunService.skipPrecheck(id);
  }

  @PostMapping("/rerun")
  public PrecheckScheduleResponse rerunCluster(@PathVariable String clusterId,
                                               @Valid @RequestBody PrecheckRerunRequest request) {

    return scheduler.rerunPrechecks(clusterId, request);
  }

  @GetMapping()
  public GetGroupedPrecheckResponse getGroupedPrechecks(@PathVariable String clusterId) {
    return precheckRunService.getGroupedPrecheckByClusterId(clusterId);
  }

  @GetMapping("/node")
  public List<GetNodePrecheckGroup> getNodePrecheckGroups(@PathVariable String clusterId) {
    return precheckRunService.getNodePrecheckGroups(clusterId);
  }

  @GetMapping("/index")
  public List<GetIndexPrecheckGroup> getIndexPrecheckGroups(@PathVariable String clusterId) {
    return precheckRunService.getIndexPrecheckGroups(clusterId);
  }

  @GetMapping("/cluster")
  public List<GetClusterPrecheckEntry> getClusterPrecheckGroups(@PathVariable String clusterId) {
    return precheckRunService.getClusterPrechecks(clusterId);
  }

  @GetMapping("/breaking-changes")
  public List<GetBreakingChangeEntry> getBreakingChanges(@PathVariable String clusterId) {
    return precheckRunService.getBreakingChanges(clusterId);
  }

  @GetMapping("/summary")
  public GetPrecheckSummaryResponse getPrecheckSummary(@PathVariable String clusterId) {
    return precheckRunService.getSummary(clusterId);
  }

  @GetMapping("/report")
  public String getReport(@PathVariable String clusterId) {
    return precheckReportService.generatePrecheckReportMdContent(clusterId);
  }

}