package co.hyperflex.prechecks.concrete.node.elastic.os;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ElasticNodeMemoryHealthPrecheck extends BaseElasticNodePrecheck {
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
    Logger logger = context.getLogger();
    ElasticClient elasticClient = context.getElasticClient();
    String nodeId = context.getNode().getId();
    var stats = elasticClient.getNodesMetric(nodeId);
    var nodeStats = stats.getNodes().get(nodeId);
    if (nodeStats == null) {
      logger.warn("No stats found for node: {}", nodeId);
      return;
    }
    var mem = nodeStats.getOs().getMem();
    long totalMemory = mem.getTotalInBytes();
    long freeMemory = mem.getFreeInBytes();
    long usedMemory = totalMemory - freeMemory;

    int memoryPercent = (int) ((usedMemory * 100.0) / totalMemory);

    long bitsInMB = 1024 * 1024;
    logger.info("Memory - Total: {} MB, Free: {} MB, Utilised: {}", totalMemory / bitsInMB,
        freeMemory / bitsInMB, memoryPercent);

    if (memoryPercent > 90) {
      logger.warn("Memory usage on node is {}", memoryPercent);
      logger.error("Memory usage check failed: current usage is {}, which exceeds the threshold of 90%", memoryPercent);
      throw new RuntimeException();
    }
  }
}
