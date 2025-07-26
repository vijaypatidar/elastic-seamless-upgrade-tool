package co.hyperflex.controllers;

import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponseModels;
import co.hyperflex.dtos.prechecks.PrecheckRerunRequest;
import co.hyperflex.dtos.prechecks.PrecheckScheduleResponse;
import co.hyperflex.prechecks.scheduler.PrecheckSchedulerService;
import co.hyperflex.services.PrecheckReportService;
import co.hyperflex.services.PrecheckRunService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping("")
  public PrecheckScheduleResponse runAll(@PathVariable String clusterId) {
    return scheduler.schedule(clusterId);
  }

  @PostMapping("/{precheckGroupId}/rerun")
  public PrecheckScheduleResponse rerunCluster(@PathVariable String clusterId,
                                               @PathVariable String precheckGroupId,
                                               @Valid @RequestBody PrecheckRerunRequest request) {
    return scheduler.rerunPrechecks(precheckGroupId, request);
  }

  @GetMapping("")
  public GetGroupedPrecheckResponseModels.GetGroupedPrecheckResponse getGroupedPrechecks(
      @PathVariable String clusterId) {
    return precheckRunService.getGroupedPrecheckByClusterId(clusterId);
  }

  @GetMapping("/report")
  public String getReport(@PathVariable String clusterId) {
    return precheckReportService.generatePrecheckReportMdContent(clusterId);
  }


}