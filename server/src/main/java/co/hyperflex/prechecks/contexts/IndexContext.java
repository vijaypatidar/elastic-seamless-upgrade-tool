package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import org.slf4j.Logger;

public class IndexContext extends PrecheckContext {
  private final String indexName;

  public IndexContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                      String indexName, ClusterUpgradeJob clusterUpgradeJob,
                      Logger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
    this.indexName = indexName;
  }

  public String getIndexName() {
    return indexName;
  }
}
