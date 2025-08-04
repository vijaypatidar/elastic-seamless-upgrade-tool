package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import org.slf4j.Logger;

public class NodeContext extends PrecheckContext {
  private final ClusterNode node;

  public NodeContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                     ClusterNode node, ClusterUpgradeJob clusterUpgradeJob, Logger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
    this.node = node;
  }

  public ClusterNode getNode() {
    return node;
  }
}
