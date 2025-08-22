package co.hyperflex.entities.cluster;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("KIBANA")
public class KibanaNodeEntity extends ClusterNodeEntity {
  public KibanaNodeEntity() {
    setType(ClusterNodeType.KIBANA);
    setUpgradable(true);
  }
}