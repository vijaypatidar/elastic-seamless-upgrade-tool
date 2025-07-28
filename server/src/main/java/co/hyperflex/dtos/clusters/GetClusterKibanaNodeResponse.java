package co.hyperflex.dtos.clusters;

public record GetClusterKibanaNodeResponse(
    String id,
    String name,
    String ip
) {
}
