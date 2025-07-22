package com.hyperflex.entities.upgrade;

import com.hyperflex.entities.cluster.ClusterNode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "node_upgrade_jobs")
public class NodeUpgradeJob {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cluster_node_id", nullable = false)
  private ClusterNode node;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cluster_upgrade_job_id", nullable = false)
  private ClusterUpgradeJob clusterUpgradeJob;

}
