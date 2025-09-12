package co.hyperflex.ai;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record SessionContext(
    @NotNull String clusterId,
    @Nullable String precheckId
) {
}
