package com.hyperflex.entities.cluster;

import com.hyperflex.entities.upgrade.ClusterNodeUpgradeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;


@Table(name = "cluster_nodes")
@Entity
public class ClusterNode {
  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String ip;

  @Column(nullable = false)
  private String version;

  @Column(nullable = false)
  private List<String> roles;

  @Enumerated(EnumType.STRING)
  private ClusterNodeType type;

  @ManyToOne
  @JoinColumn(name = "cluster_id", nullable = false)
  private Cluster cluster;

  @Column(nullable = false)
  private int progress;

  @Enumerated(EnumType.STRING)
  private ClusterNodeUpgradeStatus status;

  @Column(nullable = false)
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
