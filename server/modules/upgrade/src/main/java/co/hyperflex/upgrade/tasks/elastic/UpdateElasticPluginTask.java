package co.hyperflex.upgrade.tasks.elastic;

import co.hyperflex.pluginmanager.ElasticPluginManager;
import co.hyperflex.ssh.SshCommandExecutor;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.Task;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.List;
import org.slf4j.Logger;

public class UpdateElasticPluginTask implements Task {


  @Override
  public String getName() {
    return "Update elastic plugins";
  }

  @Override
  public TaskResult run(Context context) {
    Logger logger = context.logger();
    try (SshCommandExecutor executor = context.getSshCommandExecutor()) {
      var pluginManger = new ElasticPluginManager(executor);
      logger.info("Getting list of installed plugins");
      List<String> plugins = pluginManger.listPlugins();

      if (plugins.isEmpty()) {
        context.logger().info("No plugins found");
        return TaskResult.success("No plugins found");
      }

      logger.info("Found {} plugins[{}]", plugins.size(), String.join(", ", plugins));

      for (String plugin : plugins) {
        logger.info("Removing plugin [{}]", plugin);
        pluginManger.removePlugin(plugin);
        logger.info("Successfully removed [plugin: {}]", plugin);
        logger.info("Installing plugin [{}]", plugin);
        pluginManger.installPlugin(plugin, context.config().targetVersion());
        logger.info("Successfully installed plugin [{}]", plugin);
      }
      return TaskResult.success("Plugins updated successfully");

    } catch (Exception e) {
      logger.error("Plugin update failed: {}", e.getMessage(), e);
      return TaskResult.failure(e.getMessage());
    }
  }
}
