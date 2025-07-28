package co.hyperflex.entities.cluster;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("KIBANA")
public class KibanaNode extends ClusterNode {
  public KibanaNode() {
    setType(ClusterNodeType.KIBANA);
    setUpgradable(true);
  }
}