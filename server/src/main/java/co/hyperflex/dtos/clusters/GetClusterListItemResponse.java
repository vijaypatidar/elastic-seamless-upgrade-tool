package co.hyperflex.dtos.clusters;

import co.hyperflex.entities.cluster.ClusterType;

public record GetClusterListItemResponse(
    String id,
    String name,
    ClusterType type,
    String version,
    String status
) {
}
