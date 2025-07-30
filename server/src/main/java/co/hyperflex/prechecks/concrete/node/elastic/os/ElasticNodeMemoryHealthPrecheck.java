package co.hyperflex.prechecks.concrete.node.elastic.os;

import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.Stats;
import co.hyperflex.clients.ElasticClient;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ElasticNodeMemoryHealthPrecheck extends BaseElasticNodePrecheck {
  @Override
  public String getName() {
    return "Memory Utilization check";
  }

  @Override
  public void run(NodeContext context) {
    try {
      PrecheckLogger logger = context.getLogger();
      ElasticClient elasticClient = context.getElasticClient();
      String nodeId = context.getNode().getId();
      NodesStatsResponse stats =
          elasticClient.getElasticsearchClient().nodes().stats(r -> r.metric("os").nodeId(nodeId));

      Stats nodeStats = stats.nodes().get(nodeId);
      if (nodeStats == null) {
        logger.warn("No stats found for node: {}", nodeId);
        return;
      }

      long totalMemory = nodeStats.os().mem().totalInBytes();
      long freeMemory = nodeStats.os().mem().freeInBytes();
      long usedMemory = totalMemory - freeMemory;

      int memoryPercent = (int) ((usedMemory * 100.0) / totalMemory);

      long bitsInMB = 1024 * 1024;
      logger.info("Memory - Total: %s MB, Free: %s MB, Utilised: %s", totalMemory / bitsInMB,
          freeMemory / bitsInMB, memoryPercent);

      if (memoryPercent > 90) {
        logger.warn("Memory usage on node is %s", memoryPercent);
        throw new RuntimeException("Memory usage check failed: current usage is " + memoryPercent
            + ", which exceeds the threshold of 80%");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
