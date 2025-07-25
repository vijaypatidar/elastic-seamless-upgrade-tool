package co.hyperflex.dtos.clusters;

import co.hyperflex.entities.cluster.ClusterType;
import java.util.List;

public class AddSelfManagedClusterRequest extends AddClusterRequest {

  private List<AddClusterKibanaNodeRequest> kibanaNodes;

  public AddSelfManagedClusterRequest() {
    setType(ClusterType.SELF_MANAGED);
  }

  public List<AddClusterKibanaNodeRequest> getKibanaNodes() {
    return kibanaNodes;
  }

  public void setKibanaNodes(List<AddClusterKibanaNodeRequest> kibanaNodes) {
    this.kibanaNodes = kibanaNodes;
  }
}
