package co.hyperflex.upgrade.dtos;

import jakarta.validation.constraints.NotNull;

public record ClusterNodeUpgradeRequest(
    @NotNull String clusterId,
    @NotNull String nodeId
) {
}
