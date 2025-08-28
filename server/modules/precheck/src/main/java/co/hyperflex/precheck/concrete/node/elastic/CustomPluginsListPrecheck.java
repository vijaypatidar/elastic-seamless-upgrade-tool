package co.hyperflex.precheck.concrete.node.elastic;


import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.dto.nodes.PluginStats;
import co.hyperflex.core.models.enums.ClusterType;
import co.hyperflex.pluginmanager.PluginManagerFactory;
import co.hyperflex.precheck.contexts.NodeContext;
import co.hyperflex.precheck.core.BaseElasticNodePrecheck;
import co.hyperflex.precheck.core.enums.PrecheckSeverity;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CustomPluginsListPrecheck extends BaseElasticNodePrecheck {
  private final PluginManagerFactory pluginManagerFactory;

  public CustomPluginsListPrecheck(PluginManagerFactory pluginManagerFactory) {
    this.pluginManagerFactory = pluginManagerFactory;
  }

  @Override
  public String getName() {
    return "Manually Installed Plugins Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
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
    List<String> plugins = nodeInfo.getPlugins().stream()
        .map(PluginStats::getName)
        .filter(Objects::nonNull).toList();

    plugins.forEach(plugin -> logger.info("* {}", plugin));

    if (context.getCluster().getType() == ClusterType.SELF_MANAGED) {
      var targetVersion = context.getClusterUpgradeJob().getTargetVersion();
      logger.info("Checking plugin availability for target version [{}]", targetVersion);

      boolean unavailable = false;
      for (var plugin : plugins) {
        try {
          boolean available = pluginManagerFactory.create(null, context.getNode().getType())
              .isPluginAvailable(plugin, targetVersion);

          logger.info("* {} : {}", plugin, available ? "available" : "unavailable");
        } catch (Exception e) {
          logger.info(
              "* {} : Unable to verify plugin — it may be unavailable or no source is configured",
              plugin
          );
          unavailable = true;
        }
      }
      if (unavailable) {
        throw new RuntimeException();
      }
    }
  }
}
