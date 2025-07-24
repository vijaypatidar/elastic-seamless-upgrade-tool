package co.hyperflex.dtos;

import java.util.List;

public record GetClusterResponse(
    String id,
    String name,
    String url,
    String kibanaUrl,
    String username,
    List<GetClusterKibanaNodeResponse> kibanaNodes
) {
}
