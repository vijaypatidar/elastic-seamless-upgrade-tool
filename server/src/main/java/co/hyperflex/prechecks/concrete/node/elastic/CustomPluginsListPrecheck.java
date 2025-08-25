package co.hyperflex.prechecks.concrete.node.elastic;


import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.dto.nodes.PluginStats;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CustomPluginsListPrecheck extends BaseElasticNodePrecheck {

  private static final Set<String> BUNDLED_PLUGINS = Set.of(
      "x-pack-core",
      "x-pack-security",
      "x-pack-ml",
      "x-pack-monitoring",
      "x-pack-apm",
      "x-pack-logstash",
      "x-pack-deprecation",
      "x-pack-ilm",
      "x-pack-sql",
      "x-pack-rollup",
      "x-pack-stack",
      "x-pack-ccr",
      "x-pack-analytics",
      "repository-gcs",
      "repository-azure",
      "repository-s3"
  );

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

    List<String> installedPlugins = nodeInfo.getPlugins().stream()
        .map(PluginStats::getName)
        .filter(Objects::nonNull)
        .toList();

    List<String> customPlugins = installedPlugins.stream()
        .filter(name -> !BUNDLED_PLUGINS.contains(name))
        .toList();

    if (customPlugins.isEmpty()) {
      logger.info("Node [{}] has no manually installed plugins.", nodeInfo.getName());
    } else {
      logger.info(
          "Node [{}] has manually installed plugins: {}",
          nodeInfo.getName(),
          String.join(", ", customPlugins)
      );
    }
  }
}
