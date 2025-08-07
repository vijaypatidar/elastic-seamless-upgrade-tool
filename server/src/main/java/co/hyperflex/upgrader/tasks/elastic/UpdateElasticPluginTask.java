package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.ssh.CommandResult;
import co.hyperflex.ssh.SshCommandExecutor;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;

public class UpdateElasticPluginTask implements Task {
  @Override
  public TaskResult run(Context context) {
    var removePlugin = "sudo /usr/share/elasticsearch/bin/elasticsearch-plugin remove ";
    var installPlugin = "sudo /usr/share/elasticsearch/bin/elasticsearch-plugin install ";
    var listPlugin = "sudo /usr/share/elasticsearch/bin/elasticsearch-plugin list";
    Logger logger = context.logger();
    try (SshCommandExecutor executor = context.getSshCommandExecutor()) {
      logger.info("Getting list of installed plugins");
      CommandResult result = executor.execute(listPlugin);
      if (result.isSuccess()) {
        List<String> plugins = Arrays.stream(result.stdout().split("\n")).map(String::trim).filter(p -> !p.isBlank()).toList();

        if (plugins.isEmpty()) {
          context.logger().info("No plugins found");
          return TaskResult.success("No plugins found");
        }

        logger.info("Found {} plugins[{}]", plugins.size(), String.join(", ", plugins));

        for (String plugin : plugins) {
          logger.info("Removing plugin [{}]", plugin);
          CommandResult removePluginResult = executor.execute(removePlugin + plugin);
          if (removePluginResult.isSuccess()) {
            logger.info("Successfully removed plugin [{}]", plugin);
            logger.info("Installing plugin [{}]", plugin);
            CommandResult installPluginResult = executor.execute(installPlugin + plugin);
            if (installPluginResult.isSuccess()) {
              logger.info("Successfully installed plugin [{}]", plugin);
            } else {
              logger.info("Failed to install plugin [{}]", plugin);
              throw new RuntimeException("Failed to install plugin [" + plugin + "]");
            }
          } else {
            logger.info("Failed to remove plugin [{}]", plugin);
            throw new RuntimeException("Failed to remove plugin [" + plugin + "]");
          }
        }

        return TaskResult.success("Plugins updated successfully");
      } else {
        return TaskResult.failure("Failed to list plugins");
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
      return TaskResult.failure(e.getMessage());
    }
  }
}
