package co.hyperflex.dtos.clusters;

import co.hyperflex.entities.cluster.ClusterType;
import jakarta.validation.constraints.NotNull;

public class UpdateElasticCloudClusterRequest extends UpdateClusterRequest {

  @NotNull
  private String deploymentId;

  public UpdateElasticCloudClusterRequest() {
    setType(ClusterType.ELASTIC_CLOUD);
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

}
