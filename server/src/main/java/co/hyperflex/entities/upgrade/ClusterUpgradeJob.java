package co.hyperflex.entities.upgrade;

import co.hyperflex.entities.cluster.Cluster;

public class ClusterUpgradeJob {

  private String id;

  private Cluster cluster;

  private String currentVersion;
  private String targetVersion;

  private ClusterUpgradeStatus status;

}
