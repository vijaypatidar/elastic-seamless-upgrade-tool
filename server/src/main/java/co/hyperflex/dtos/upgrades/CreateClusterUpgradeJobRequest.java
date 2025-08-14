package co.hyperflex.dtos.upgrades;

import jakarta.validation.constraints.NotNull;

public record CreateClusterUpgradeJobRequest(
    @NotNull String targetVersion
) {
}
