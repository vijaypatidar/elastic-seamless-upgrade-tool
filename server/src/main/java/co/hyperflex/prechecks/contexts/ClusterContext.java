package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.entities.upgrade.ClusterUpgradeJobEntity;
import org.slf4j.Logger;

public class ClusterContext extends PrecheckContext {
  public ClusterContext(ClusterEntity cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                        ClusterUpgradeJobEntity clusterUpgradeJob, Logger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
  }
}
