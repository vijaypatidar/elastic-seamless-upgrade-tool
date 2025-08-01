package co.hyperflex.dtos.clusters;

public record GetClusterListItemResponse(
    String id,
    String name,
    String type,
    String version,
    String status
) {
}
