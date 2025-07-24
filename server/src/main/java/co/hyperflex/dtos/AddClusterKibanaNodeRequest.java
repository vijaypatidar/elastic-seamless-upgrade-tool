package co.hyperflex.dtos;

import jakarta.validation.constraints.NotNull;

public record AddClusterKibanaNodeRequest(@NotNull String name, @NotNull String ip) {
}
