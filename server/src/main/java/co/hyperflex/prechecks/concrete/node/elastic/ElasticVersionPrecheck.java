package co.hyperflex.prechecks.concrete.node.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.NodesInfoResponse;
import co.elastic.clients.elasticsearch.nodes.info.NodeInfo;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ElasticVersionPrecheck extends BaseElasticNodePrecheck {


  public ElasticVersionPrecheck() {
  }

  @Override
  public String getName() {
    return "Elasticsearch Version Check";
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();

    String expectedVersion = context.getClusterUpgradeJob().getCurrentVersion();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    PrecheckLogger logger = context.getLogger();

    try {
      NodesInfoResponse response = client.nodes().info(r -> r.nodeId(nodeId));
      Map<String, NodeInfo> nodes = response.nodes();
      NodeInfo node = nodes.get(nodeId);

      if (node == null) {
        throw new RuntimeException("Node with ID [" + nodeId + "] not found.");
      }

      String actualVersion = node.version();
      if (expectedVersion.equals(actualVersion)) {
        logger.info("Node [%s] is running on the expected version: %s.", node.name(),
            expectedVersion);
      } else {
        String msg = String.format(
            "Node [%s] version mismatch: expected %s, but found %s.",
            node.name(), expectedVersion, actualVersion
        );
        logger.error(msg);
        throw new RuntimeException(msg); // Similar to throwing ConflictError in Node.js
      }

    } catch (IOException e) {
      logger.error("Failed to get version info for node: {}", nodeId, e);
      throw new RuntimeException("Failed to get version info for node: " + nodeId, e);
    }
  }
}
