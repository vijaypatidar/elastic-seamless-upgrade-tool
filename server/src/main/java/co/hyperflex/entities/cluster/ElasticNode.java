package co.hyperflex.entities.cluster;


import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ELASTIC")
public class ElasticNode extends ClusterNode {
  public ElasticNode() {
    setType(ClusterNodeType.ELASTIC);
  }

  private boolean isMaster;

  public boolean isMaster() {
    return isMaster;
  }

  public void setMaster(boolean master) {
    isMaster = master;
  }
}