package co.hyperflex.prechecks.concrete.node.kibana.os;

import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.clients.kibana.dto.GetKibanaStatusResponse;
import co.hyperflex.clients.kibana.dto.OsStats;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseKibanaNodePrecheck;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class KibanaNodeCpuHealthPrecheck extends BaseKibanaNodePrecheck {
  @Override
  public String getName() {
    return "CPU Utilization check";
  }

  @Override
  public void run(NodeContext context) {
    Logger logger = context.getLogger();
    String nodeId = context.getNode().getId();
    KibanaClient kibanaClient = context.getKibanaClient();

    GetKibanaStatusResponse details =
        kibanaClient.getKibanaNodeDetails(context.getNode().getIp());

    OsStats osStats = details.metrics().os();

    if (osStats == null) {
      logger.warn("No stats found for node: {}", nodeId);
      return;
    }

    double cpuPercent = osStats.load().fiveMinute();

    logger.info("CPU Utilization check completed for node: %s", nodeId);
    logger.info("CPU Usage: " + cpuPercent + "%%");
    if (cpuPercent > 80) {
      throw new RuntimeException("CPU utilization check failed: current usage is " + cpuPercent
          + "%, which exceeds the threshold of 80%");
    }

  }
}
