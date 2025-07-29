package co.hyperflex.upgrader.tasks;

import co.hyperflex.ansible.AnsibleAdHocCommand;
import co.hyperflex.ansible.AnsibleService;
import java.util.function.Consumer;

public abstract class AbstractAnsibleTask implements Task {

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
      return TaskResult.failure("Exception while running Ansible: " + e.getMessage());
    }
  }
}
