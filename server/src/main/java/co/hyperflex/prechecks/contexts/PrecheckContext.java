package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.entities.upgrade.ClusterUpgradeJobEntity;
import org.slf4j.Logger;

public abstract class PrecheckContext {
  private final ClusterEntity cluster;
  private final ElasticClient elasticClient;
  private final KibanaClient kibanaClient;
  private final Logger logger;
  private final ClusterUpgradeJobEntity clusterUpgradeJob;

  protected PrecheckContext(ClusterEntity cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                            Logger logger, ClusterUpgradeJobEntity clusterUpgradeJob) {
    this.cluster = cluster;
    this.elasticClient = elasticClient;
    this.kibanaClient = kibanaClient;
    this.logger = logger;
    this.clusterUpgradeJob = clusterUpgradeJob;
  }

  public ClusterEntity getCluster() {
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

  public ClusterUpgradeJobEntity getClusterUpgradeJob() {
    return clusterUpgradeJob;
  }
}
