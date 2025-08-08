package co.hyperflex.prechecks.concrete.node.kibana;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseKibanaNodePrecheck;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class KibanaVersionPrecheck extends BaseKibanaNodePrecheck {

  @Override
  public String getName() {
    return "Kibana Version Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    String expectedVersion = context.getClusterUpgradeJob().getCurrentVersion();

    String nodeIp = context.getNode().getIp();
    Logger logger = context.getLogger();

    try {
      String actualVersion = context.getKibanaClient().getKibanaVersion(nodeIp);
      if (expectedVersion.equals(actualVersion)) {
        logger.info("Kibana node [{}] is running on the expected version: {}.", nodeIp,
            expectedVersion);
      } else {
        logger.error(
            "Kibana node [{}] version mismatch: expected {}, but found {}.",
            nodeIp, expectedVersion, actualVersion
        );
        throw new RuntimeException();
      }
    } catch (Exception e) {
      logger.error("Node with IP [{}] not found or unreachable.", nodeIp);
      throw new RuntimeException(e);
    }
  }
}
