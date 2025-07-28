package co.hyperflex.dtos.upgrades;

import jakarta.validation.constraints.NotNull;

public record ClusterUpgradeResponse(@NotNull String message) {
}
