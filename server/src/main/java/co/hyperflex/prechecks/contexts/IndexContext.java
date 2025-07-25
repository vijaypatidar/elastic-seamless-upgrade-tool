package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.prechecks.core.PrecheckLogger;

public class IndexContext extends PrecheckContext {
  private final String indexName;

  public IndexContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                      String indexName, PrecheckLogger logger) {
    super(cluster, elasticClient, kibanaClient, logger);
    this.indexName = indexName;
  }

  public String getIndexName() {
    return indexName;
  }
}
