package co.hyperflex.upgrade.dtos;

import jakarta.validation.constraints.NotNull;

public record CreateClusterUpgradeJobRequest(
    @NotNull String targetVersion
) {
}
