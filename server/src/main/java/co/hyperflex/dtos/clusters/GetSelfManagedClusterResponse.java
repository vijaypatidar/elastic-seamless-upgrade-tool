package co.hyperflex.dtos.clusters;

import co.hyperflex.entities.cluster.ClusterType;
import java.util.List;

public class GetSelfManagedClusterResponse extends GetClusterResponse {
  private List<GetClusterKibanaNodeResponse> kibanaNodes;

  public GetSelfManagedClusterResponse() {
    setType(ClusterType.SELF_MANAGED);
  }

  public List<GetClusterKibanaNodeResponse> getKibanaNodes() {
    return kibanaNodes;
  }

  public void setKibanaNodes(List<GetClusterKibanaNodeResponse> kibanaNodes) {
    this.kibanaNodes = kibanaNodes;
  }
}
