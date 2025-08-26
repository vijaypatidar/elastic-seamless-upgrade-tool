package co.hyperflex.dtos.upgrades;

import co.hyperflex.core.models.enums.ClusterUpgradeStatus;
import jakarta.validation.constraints.NotNull;

public record GetUpgradeJobStatusResponse(
    @NotNull boolean isStopping,
    @NotNull ClusterUpgradeStatus status
) {
}
