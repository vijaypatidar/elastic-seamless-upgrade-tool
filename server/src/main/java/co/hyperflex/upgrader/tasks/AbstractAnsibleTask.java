package co.hyperflex.upgrader.tasks;

import co.hyperflex.ansible.AnsibleService;
import co.hyperflex.ansible.commands.AnsibleAdHocCommand;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAnsibleTask implements Task {

  private static final Logger logger = LoggerFactory.getLogger(AbstractAnsibleTask.class);
  private final AnsibleService ansibleService = new AnsibleService();

  protected TaskResult runAdHocCommand(AnsibleAdHocCommand cmd, Context context) {
    try {
      StringBuilder output = new StringBuilder();
      Consumer<String> consumer = s -> output.append(s).append("\n");

      int exitCode = ansibleService.run(cmd, consumer, consumer);

      if (exitCode == 0) {
        return TaskResult.success(output.toString());
      } else {
        return TaskResult.failure(output.toString());
      }

    } catch (Exception e) {
      logger.error("Exception while running Ansible for host {}", cmd.getHostIp(), e);
      return TaskResult.failure("Exception while running Ansible: " + e.getMessage());
    }
  }
}
