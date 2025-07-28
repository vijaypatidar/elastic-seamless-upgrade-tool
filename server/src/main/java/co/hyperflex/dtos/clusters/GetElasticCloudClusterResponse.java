package co.hyperflex.dtos.clusters;

import co.hyperflex.entities.cluster.ClusterType;

public class GetElasticCloudClusterResponse extends GetClusterResponse {
  private String deploymentId;

  public GetElasticCloudClusterResponse() {
    setType(ClusterType.ELASTIC_CLOUD);
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

}
