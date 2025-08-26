package co.hyperflex.upgrade.dtos;

import jakarta.validation.constraints.NotNull;

public record ClusterNodeUpgradeResponse(@NotNull String message) {
}
