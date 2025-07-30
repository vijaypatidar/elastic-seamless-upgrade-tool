package co.hyperflex.controllers;

import co.hyperflex.dtos.ClusterInfoResponse;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeRequest;
import co.hyperflex.dtos.upgrades.ClusterNodeUpgradeResponse;
import co.hyperflex.dtos.upgrades.ClusterUpgradeResponse;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobRequest;
import co.hyperflex.dtos.upgrades.CreateClusterUpgradeJobResponse;
import co.hyperflex.services.ClusterUpgradeJobService;
import co.hyperflex.services.ClusterUpgradeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/upgrades")
public class UpgradeController {
  private static final Logger logger = LoggerFactory.getLogger(UpgradeController.class);
  private final ClusterUpgradeService clusterUpgradeService;
  private final ClusterUpgradeJobService createClusterUpgradeJob;

  public UpgradeController(ClusterUpgradeService clusterUpgradeService,
                           ClusterUpgradeJobService createClusterUpgradeJob) {
    this.clusterUpgradeService = clusterUpgradeService;
    this.createClusterUpgradeJob = createClusterUpgradeJob;
  }


  @PostMapping("/jobs")
  public CreateClusterUpgradeJobResponse clusterUpgradeJob(
      @Valid @RequestBody CreateClusterUpgradeJobRequest request) {
    return createClusterUpgradeJob.createClusterUpgradeJob(request);
  }

  @PostMapping("/nodes")
  public ClusterNodeUpgradeResponse clusterNodeUpgrade(
      @Valid @RequestBody ClusterNodeUpgradeRequest request) {
    return clusterUpgradeService.upgradeNode(request);
  }

  @PostMapping("/clusters/{clusterId}")
  public ClusterUpgradeResponse clusterUpgrade(@PathVariable String clusterId) {
    return clusterUpgradeService.upgrade(clusterId);
  }

  @GetMapping("/{clusterId}/info")
  public ClusterInfoResponse clusterInfo(@PathVariable String clusterId) {
    return clusterUpgradeService.upgradeInfo(clusterId);
  }

}
