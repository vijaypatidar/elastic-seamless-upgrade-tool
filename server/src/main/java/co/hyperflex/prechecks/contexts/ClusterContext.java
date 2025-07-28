package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.prechecks.core.PrecheckLogger;

public class ClusterContext extends PrecheckContext {
  public ClusterContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                        PrecheckLogger logger) {
    super(cluster, elasticClient, kibanaClient, logger);
  }
}
