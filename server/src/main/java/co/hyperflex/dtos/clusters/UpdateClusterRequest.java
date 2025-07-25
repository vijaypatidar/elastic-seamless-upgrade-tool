package co.hyperflex.dtos.clusters;


import java.util.List;

public record UpdateClusterRequest(
    String name,
    String url,
    String kibanaUrl,
    String username,
    List<AddClusterKibanaNodeRequest> kibanaNodes
) {
}