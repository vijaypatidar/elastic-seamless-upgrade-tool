package com.hyperflex.dtos;


import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddClusterRequest(
    @NotNull String name,
    @NotNull String url,
    @NotNull String kibanaUrl,
    @NotNull String username,
    @NotNull String password,
    @NotNull List<AddClusterKibanaNodeRequest> kibanaNodes
) {
}