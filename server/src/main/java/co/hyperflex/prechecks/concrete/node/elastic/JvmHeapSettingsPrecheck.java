package co.hyperflex.prechecks.concrete.node.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.NodesInfoResponse;
import co.elastic.clients.elasticsearch.nodes.info.NodeInfo;
import co.elastic.clients.elasticsearch.nodes.info.NodeInfoJvmMemory;
import co.elastic.clients.elasticsearch.nodes.info.NodeJvmInfo;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JvmHeapSettingsPrecheck extends BaseElasticNodePrecheck {

  @Override
  public String getName() {
    return "JVM heap settings check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    PrecheckLogger logger = context.getLogger();

    try {
      NodesInfoResponse response = client.nodes().info(r -> r.nodeId(nodeId).metric("jvm"));

      Map<String, NodeInfo> nodes = response.nodes();
      NodeInfo node = nodes.get(nodeId);

      if (node == null) {
        throw new RuntimeException("Node with ID [" + nodeId + "] not found");
      }

      String name = node.name();
      NodeJvmInfo jvmStats = node.jvm();
      if (jvmStats == null || jvmStats.mem() == null) {
        logger.info("%s: Skipping JVM heap check — JVM memory stats missing.", name);
        return;
      }

      NodeInfoJvmMemory memStats = jvmStats.mem();
      long heapInit = memStats.heapInitInBytes();
      long heapMax = memStats.heapMaxInBytes();

      double heapInitGB = heapInit / Math.pow(1024, 3);
      double heapMaxGB = heapMax / Math.pow(1024, 3);

      logger.info("%s: -Xms=%.2fGB, -Xmx=%.2fGB", name, heapInitGB, heapMaxGB);

      if (heapInit != heapMax) {
        throw new RuntimeException(
            String.format("%s has mismatched -Xms and -Xmx values. They must be equal.", name));
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to fetch JVM info for node: " + nodeId, e);
    }
  }
}
