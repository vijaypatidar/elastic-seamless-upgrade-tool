package com.hyperflex.entities.upgrade;

import com.hyperflex.entities.cluster.Cluster;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cluster_upgrade_jobs")
public class ClusterUpgradeJob {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "cluster_id", nullable = false)
  private Cluster cluster;

  private String currentVersion;
  private String targetVersion;

  @Enumerated(EnumType.STRING)
  private ClusterUpgradeStatus status;

}
