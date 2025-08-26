package co.hyperflex.precheck.concrete.node.elastic;


import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.dto.nodes.PluginStats;
import co.hyperflex.precheck.contexts.NodeContext;
import co.hyperflex.precheck.core.BaseElasticNodePrecheck;
import co.hyperflex.precheck.core.enums.PrecheckSeverity;
import java.util.Objects;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CustomPluginsListPrecheck extends BaseElasticNodePrecheck {

  @Override
  public String getName() {
    return "Manually Installed Plugins Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.INFO;
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();
    ElasticClient client = context.getElasticClient();
    Logger logger = context.getLogger();

    var nodeInfoResponse = client.getNodeInfo(nodeId);

    var nodes = nodeInfoResponse.getNodes();
    var nodeInfo = nodes.get(nodeId);

    if (nodeInfo == null) {
      throw new RuntimeException("Node with ID [" + nodeId + "] not found.");
    }

    if (nodeInfo.getPlugins() == null || nodeInfo.getPlugins().isEmpty()) {
      logger.info("No plugins found for node with ID [{}].", nodeId);
      return;
    }

    logger.info("Node [{}] has manually installed plugins:", nodeInfo.getName());
    nodeInfo.getPlugins().stream()
        .map(PluginStats::getName)
        .filter(Objects::nonNull)
        .forEach(plugin -> logger.info("* {}", plugin));
  }
}
