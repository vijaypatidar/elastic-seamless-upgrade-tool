package co.hyperflex.controllers;

import co.hyperflex.dtos.ClusterInfoResponse;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeRequest;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeResponse;
import co.hyperflex.dtos.upgrades.ClusterUpgradeResponse;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobRequest;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobResponse;
import co.hyperflex.dtos.upgrades.GetUpgradeLogsRequest;
import co.hyperflex.dtos.upgrades.GetUpgradeLogsResponse;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.services.ClusterUpgradeJobService;
import co.hyperflex.services.ClusterUpgradeService;
import co.hyperflex.services.UpgradeLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clusters/{clusterId}/upgrades")
public class UpgradeController {
  private static final Logger logger = LoggerFactory.getLogger(UpgradeController.class);
  private final ClusterUpgradeService clusterUpgradeService;
  private final ClusterUpgradeJobService createClusterUpgradeJob;
  private final UpgradeLogService upgradeLogService;

  public UpgradeController(ClusterUpgradeService clusterUpgradeService, ClusterUpgradeJobService createClusterUpgradeJob,
                           UpgradeLogService upgradeLogService) {
    this.clusterUpgradeService = clusterUpgradeService;
    this.createClusterUpgradeJob = createClusterUpgradeJob;
    this.upgradeLogService = upgradeLogService;
  }

  @PostMapping()
  public ClusterUpgradeResponse clusterUpgrade(@PathVariable String clusterId, @RequestParam ClusterNodeType nodeType) {
    return clusterUpgradeService.upgrade(clusterId, nodeType);
  }

  @PostMapping("/jobs")
  public CreateClusterUpgradeJobResponse clusterUpgradeJob(@Valid @RequestBody CreateClusterUpgradeJobRequest request,
                                                           @PathVariable String clusterId) {
    return createClusterUpgradeJob.createClusterUpgradeJob(clusterId, request);
  }

  @PostMapping("/nodes/{nodeId}")
  public ClusterNodeUpgradeResponse clusterNodeUpgrade(@PathVariable String clusterId,
                                                       @PathVariable String nodeId) {
    return clusterUpgradeService.upgradeNode(new ClusterNodeUpgradeRequest(clusterId, nodeId));
  }


  @GetMapping("info")
  public ClusterInfoResponse clusterInfo(@PathVariable String clusterId) {
    return clusterUpgradeService.upgradeInfo(clusterId);
  }

  @GetMapping("/nodes/{nodeId}/logs")
  public GetUpgradeLogsResponse clusterUpgradeLogs(@PathVariable String clusterId, @PathVariable String nodeId) {
    ClusterUpgradeJob job = createClusterUpgradeJob.getLatestJobByClusterId(clusterId);
    return upgradeLogService.getLogs(new GetUpgradeLogsRequest(job.getId(), nodeId));
  }

}
