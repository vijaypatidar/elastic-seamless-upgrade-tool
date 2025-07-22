package com.hyperflex.dtos;

public record GetClusterKibanaNodeResponse(
        String id,
        String name,
        String ip
) {
}
