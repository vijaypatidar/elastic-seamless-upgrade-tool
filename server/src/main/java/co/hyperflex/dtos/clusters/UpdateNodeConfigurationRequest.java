package co.hyperflex.dtos.clusters;

import jakarta.validation.constraints.NotNull;

public record UpdateNodeConfigurationRequest(@NotNull String config) {
}
