package co.hyperflex.dtos.clusters;

public record ClusterListItemResponse(
    String id,
    String name,
    String type,
    String typeDisplayName,
    String version,
    String status
) {
}
