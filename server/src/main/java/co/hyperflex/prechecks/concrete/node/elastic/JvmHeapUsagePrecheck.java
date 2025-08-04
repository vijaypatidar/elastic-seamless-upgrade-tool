package co.hyperflex.prechecks.concrete.node.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.Jvm;
import co.elastic.clients.elasticsearch.nodes.JvmMemoryStats;
import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.Stats;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JvmHeapUsagePrecheck extends BaseElasticNodePrecheck {

  private static final double THRESHOLD_PERCENT = 75.0;

  @Override
  public String getName() {
    return "JVM Heap Usage Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    Logger logger = context.getLogger();

    try {
      NodesStatsResponse response = client.nodes().stats(r -> r
          .nodeId(nodeId)
          .metric("jvm")
      );

      Map<String, Stats> nodes = response.nodes();
      Stats node = nodes.get(nodeId);

      if (node == null) {
        throw new RuntimeException("Node with ID [" + nodeId + "] not found");
      }

      String name = node.name();
      Jvm jvm = node.jvm();
      if (jvm == null || jvm.mem() == null) {
        logger.info("%s: Skipping heap usage check — missing JVM memory stats.", name);
        return;
      }

      JvmMemoryStats mem = jvm.mem();
      Long heapUsed = mem.heapUsedInBytes();
      Long heapMax = mem.heapMaxInBytes();

      if (heapUsed == null || heapMax == null) {
        logger.info("%s: Skipping heap usage check — missing heap values.", name);
        return;
      }

      double usedPercent = (double) heapUsed / heapMax * 100;
      double heapUsedGB = heapUsed / Math.pow(1024, 3);
      double heapMaxGB = heapMax / Math.pow(1024, 3);

      logger.info("%s: Heap used = %.2fGB / %.2fGB (%.2f%%)", name, heapUsedGB, heapMaxGB,
          usedPercent);

      if (usedPercent >= THRESHOLD_PERCENT) {
        String msg = String.format(
            "%s: Heap usage is too high (%.2f%%). It must be below %.0f%%.",
            name, usedPercent, THRESHOLD_PERCENT
        );
        logger.warn(msg);
        throw new RuntimeException(msg); // Replace with ConflictException if defined
      }

    } catch (IOException e) {
      logger.error("Failed to get JVM stats for node: {}", nodeId, e);
      throw new RuntimeException("Failed to get JVM stats for node: " + nodeId, e);
    }
  }
}
