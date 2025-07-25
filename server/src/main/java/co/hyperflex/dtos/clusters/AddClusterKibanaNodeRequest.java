package co.hyperflex.dtos.clusters;

import jakarta.validation.constraints.NotNull;

public record AddClusterKibanaNodeRequest(@NotNull String name, @NotNull String ip) {
}
