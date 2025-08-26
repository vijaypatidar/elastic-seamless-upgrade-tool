package co.hyperflex.upgrade.dtos;

import jakarta.validation.constraints.NotNull;

public record ClusterUpgradeResponse(@NotNull String message) {
}
