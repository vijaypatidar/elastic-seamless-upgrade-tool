package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import org.slf4j.Logger;

public class ClusterContext extends PrecheckContext {
  public ClusterContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                        ClusterUpgradeJob clusterUpgradeJob, Logger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
  }
}
