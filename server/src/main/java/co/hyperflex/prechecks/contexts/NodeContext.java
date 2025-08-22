package co.hyperflex.prechecks.contexts;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.entities.cluster.ClusterNodeEntity;
import co.hyperflex.entities.cluster.SelfManagedClusterEntity;
import co.hyperflex.entities.upgrade.ClusterUpgradeJobEntity;
import co.hyperflex.ssh.SshCommandExecutor;
import org.slf4j.Logger;

public class NodeContext extends PrecheckContext {
  private final ClusterNodeEntity node;

  public NodeContext(ClusterEntity cluster, ElasticClient elasticClient, KibanaClient kibanaClient,
                     ClusterNodeEntity node, ClusterUpgradeJobEntity clusterUpgradeJob, Logger logger) {
    super(cluster, elasticClient, kibanaClient, logger, clusterUpgradeJob);
    this.node = node;
  }

  public ClusterNodeEntity getNode() {
    return node;
  }

  public SshCommandExecutor getSshExecutor() {
    if (getCluster() instanceof SelfManagedClusterEntity selfManagedCluster) {
      return new SshCommandExecutor(
          node.getIp(),
          22,
          selfManagedCluster.getSshInfo().username(),
          selfManagedCluster.getSshInfo().keyPath());
    } else {
      throw new IllegalStateException("SshCommandExecutor not supported yet for this type of cluster");
    }
  }
}
