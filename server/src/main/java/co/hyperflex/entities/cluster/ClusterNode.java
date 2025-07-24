package co.hyperflex.entities.cluster;

import co.hyperflex.entities.upgrade.ClusterNodeUpgradeStatus;
import java.util.List;


public class ClusterNode {
  private String id;


  private String name;

  private String ip;

  private String version;

  private List<String> roles;

  private ClusterNodeType type;

  private Cluster cluster;

  private int progress;

  private ClusterNodeUpgradeStatus status;

  private String os;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public ClusterNodeType getType() {
    return type;
  }

  public void setType(ClusterNodeType type) {
    this.type = type;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public ClusterNodeUpgradeStatus getStatus() {
    return status;
  }

  public void setStatus(ClusterNodeUpgradeStatus status) {
    this.status = status;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }
}
