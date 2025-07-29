package co.hyperflex.prechecks.concrete.node.kibana;

import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseKibanaNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import org.springframework.stereotype.Component;

@Component
public class KibanaVersionPrecheck extends BaseKibanaNodePrecheck {

  @Override
  public String getName() {
    return "Kibana Version Check";
  }

  @Override
  public void run(NodeContext context) {
    String expectedVersion = context.getClusterUpgradeJob().getCurrentVersion();

    String nodeIp = context.getNode().getIp();
    PrecheckLogger logger = context.getLogger();

    try {
      String actualVersion = context.getKibanaClient().getKibanaVersion(nodeIp);
      if (expectedVersion.equals(actualVersion)) {
        logger.info("Kibana node [%s] is running on the expected version: %s.", nodeIp,
            expectedVersion);
      } else {
        String msg = String.format(
            "Kibana node [%s] version mismatch: expected %s, but found %s.",
            nodeIp, expectedVersion, actualVersion
        );
        logger.error(msg);
        throw new RuntimeException(msg); // Use ConflictException if defined
      }
    } catch (Exception e) {
      String msg = String.format("Node with IP [%s] not found or unreachable.", nodeIp);
      logger.error(msg);
      throw new RuntimeException(msg, e); // Use NotFoundException if defined
    }
  }
}
