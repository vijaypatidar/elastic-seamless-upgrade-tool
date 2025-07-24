package co.hyperflex.controllers;

import co.hyperflex.dtos.AddClusterRequest;
import co.hyperflex.dtos.AddClusterResponse;
import co.hyperflex.dtos.GetClusterNodeResponse;
import co.hyperflex.dtos.GetClusterResponse;
import co.hyperflex.dtos.UpdateClusterRequest;
import co.hyperflex.dtos.UpdateClusterResponse;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.services.ClusterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clusters")
public class ClusterController {

  private final ClusterService clusterService;

  public ClusterController(ClusterService clusterService) {
    this.clusterService = clusterService;
  }

  @PostMapping
  public AddClusterResponse addCluster(@Valid @RequestBody AddClusterRequest request) {
    return clusterService.add(request);
  }

  @PutMapping("/{clusterId}")
  public UpdateClusterResponse updateCluster(@Valid @RequestBody UpdateClusterRequest request,
                                             @PathVariable String clusterId) {
    return new UpdateClusterResponse();
  }

  @GetMapping("/{clusterId}")
  public GetClusterResponse getCluster(@PathVariable String clusterId) {
    return clusterService.getClusterById(clusterId);
  }

  @GetMapping("/{clusterId}/nodes")
  public List<GetClusterNodeResponse> getAllClusters(@PathVariable String clusterId,
                                                     @RequestParam(required = false)
                                                     ClusterNodeType type) {
    return clusterService.getNodes(clusterId, type);
  }

}
