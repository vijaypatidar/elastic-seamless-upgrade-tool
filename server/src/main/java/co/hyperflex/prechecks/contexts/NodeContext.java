package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.prechecks.core.PrecheckLogger;

public class NodeContext extends PrecheckContext {
  private final ClusterNode node;

  public NodeContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                     ClusterNode node, ClusterUpgradeJob clusterUpgradeJob, PrecheckLogger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
    this.node = node;
  }

  public ClusterNode getNode() {
    return node;
  }
}
