package co.hyperflex.dtos;

public record GetClusterKibanaNodeResponse(
    String id,
    String name,
    String ip
) {
}
