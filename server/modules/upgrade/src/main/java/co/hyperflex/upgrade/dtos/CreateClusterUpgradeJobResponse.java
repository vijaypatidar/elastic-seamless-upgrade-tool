package co.hyperflex.upgrade.dtos;

import jakarta.validation.constraints.NotNull;

public record CreateClusterUpgradeJobResponse(
    @NotNull String message,
    @NotNull String id
) {
}
