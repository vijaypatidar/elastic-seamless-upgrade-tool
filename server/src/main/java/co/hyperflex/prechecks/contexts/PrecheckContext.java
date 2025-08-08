package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import org.slf4j.Logger;

public abstract class PrecheckContext {
  private final Cluster cluster;
  private final ElasticClient elasticClient;
  private final KibanaClient kibanaClient;
  private final Logger logger;
  private final ClusterUpgradeJob clusterUpgradeJob;

  protected PrecheckContext(Cluster cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                            Logger logger, ClusterUpgradeJob clusterUpgradeJob) {
    this.cluster = cluster;
    this.elasticClient = elasticClient;
    this.kibanaClient = kibanaClient;
    this.logger = logger;
    this.clusterUpgradeJob = clusterUpgradeJob;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public ElasticClient getElasticClient() {
    return elasticClient;
  }

  public KibanaClient getKibanaClient() {
    return kibanaClient;
  }

  public Logger getLogger() {
    return logger;
  }

  public ClusterUpgradeJob getClusterUpgradeJob() {
    return clusterUpgradeJob;
  }
}
