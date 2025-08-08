package co.hyperflex.dtos.upgrades;

import jakarta.validation.constraints.NotNull;

public record GetUpgradeLogsRequest(
    @NotNull String clusterUpgradeJobId,
    @NotNull String nodeId
) {
}
