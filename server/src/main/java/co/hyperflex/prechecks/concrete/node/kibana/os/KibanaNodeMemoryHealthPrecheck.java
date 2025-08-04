package co.hyperflex.prechecks.concrete.node.kibana.os;

import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.clients.kibana.dto.GetKibanaStatusResponse;
import co.hyperflex.clients.kibana.dto.OsStats;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseKibanaNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import org.springframework.stereotype.Component;

@Component
public class KibanaNodeMemoryHealthPrecheck extends BaseKibanaNodePrecheck {
  @Override
  public String getName() {
    return "Memory Utilization check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    PrecheckLogger logger = context.getLogger();
    String nodeId = context.getNode().getId();
    KibanaClient kibanaClient = context.getKibanaClient();

    GetKibanaStatusResponse details =
        kibanaClient.getKibanaNodeDetails(context.getNode().getIp());

    OsStats osStats = details.metrics().os();

    if (osStats == null) {
      logger.warn("No stats found for node: {}", nodeId);
      return;
    }

    long totalMemory = osStats.memory().totalInBytes();
    long freeMemory = osStats.memory().freeInBytes();
    long usedMemory = totalMemory - freeMemory;

    int memoryPercent = (int) ((usedMemory * 100.0) / totalMemory);

    long bitsInMB = 1024 * 1024;
    logger.info("Memory - Total: %s MB, Free: %s MB, Utilised: %s%%", totalMemory / bitsInMB,
        freeMemory / bitsInMB, memoryPercent);

    if (memoryPercent > 90) {
      logger.warn("Memory usage on node is %s", memoryPercent);
      throw new RuntimeException("Memory usage check failed: current usage is " + memoryPercent
          + ", which exceeds the threshold of 90%");
    }
  }
}
