package com.hyperflex.controllers;

import com.hyperflex.dtos.AddClusterRequest;
import com.hyperflex.dtos.AddClusterResponse;
import com.hyperflex.dtos.GetClusterNodeResponse;
import com.hyperflex.dtos.GetClusterResponse;
import com.hyperflex.dtos.UpdateClusterRequest;
import com.hyperflex.dtos.UpdateClusterResponse;
import com.hyperflex.entities.cluster.ClusterNodeType;
import com.hyperflex.services.ClusterService;
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
  public UpdateClusterResponse updateCluster(@Valid @RequestBody UpdateClusterRequest request) {
    return new UpdateClusterResponse();
  }

  @GetMapping("/{clusterId}")
  public GetClusterResponse getCluster(@PathVariable String clusterId) {
    return null;
  }

  @GetMapping("/{clusterId}/nodes")
  public List<GetClusterNodeResponse> getAllClusters(@PathVariable String clusterId,
                                                     @RequestParam(required = false)
                                                     ClusterNodeType type) {
    return List.of();
  }

}
