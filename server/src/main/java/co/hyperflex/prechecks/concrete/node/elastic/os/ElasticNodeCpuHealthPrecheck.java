package co.hyperflex.prechecks.concrete.node.elastic.os;

import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.Stats;
import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import java.io.IOException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ElasticNodeCpuHealthPrecheck extends BaseElasticNodePrecheck {
  @Override
  public String getName() {
    return "CPU Utilization check";
  }

  @Override
  public void run(NodeContext context) {
    try {
      Logger logger = context.getLogger();
      ElasticClient elasticClient = context.getElasticClient();
      String nodeId = context.getNode().getId();
      NodesStatsResponse stats =
          elasticClient.getElasticsearchClient().nodes().stats(r -> r.metric("os").nodeId(nodeId));

      Stats nodeStats = stats.nodes().get(nodeId);
      if (nodeStats == null) {
        logger.warn("No stats found for node: {}", nodeId);
        return;
      }

      int cpuPercent = nodeStats.os().cpu().percent();

      logger.info("CPU Utilization check completed for node: {}", nodeId);
      logger.info("CPU Usage: {}%", cpuPercent);
      if (cpuPercent > 80) {
        logger.error("CPU Utilization check failed. Current CPU usage is {}%, which exceeds the threshold of 80%", cpuPercent);
        throw new RuntimeException();
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
