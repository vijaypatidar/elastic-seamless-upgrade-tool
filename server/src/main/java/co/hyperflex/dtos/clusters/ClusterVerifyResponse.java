package co.hyperflex.dtos.clusters;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Deprecated
public record ClusterVerifyResponse(
    @NotNull boolean clusterAvailable,
    @Nullable GetClusterResponse cluster
) {
}
