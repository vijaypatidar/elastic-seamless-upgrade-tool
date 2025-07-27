package co.hyperflex.services;

import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.ClusterUpgradeStatus;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterUpgradeJobRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ClusterUpgradeJobService {
  private final ClusterUpgradeJobRepository clusterUpgradeJobRepository;

  public ClusterUpgradeJobService(ClusterUpgradeJobRepository clusterUpgradeJobRepository) {
    this.clusterUpgradeJobRepository = clusterUpgradeJobRepository;
  }

  public @NotNull ClusterUpgradeJob getActiveClusterJobByClusterId(@NotNull String clusterId) {
    return clusterUpgradeJobRepository
        .findByClusterIdAndStatusIsNot(clusterId, ClusterUpgradeStatus.UPDATED)
        .stream().findFirst().orElseThrow(
            () -> new NotFoundException("No active cluster job found for clusterId: " + clusterId));
  }
}
