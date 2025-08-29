package co.hyperflex.upgrade.tasks.elastic;

import co.hyperflex.ansible.commands.AnsibleAdHocAptCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocDnfCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocYumCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UpdateElasticsearchTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Update elasticsearch";
  }

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocCommand command = switch (context.node().getOs().packageManager()) {
      case APT -> new AnsibleAdHocAptCommand.Builder()
          .hostIp(context.node().getIp())
          .args(Map.of(
              "name", "elasticsearch=" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();

      case DNF -> new AnsibleAdHocDnfCommand.Builder()
          .hostIp(context.node().getIp())
          .args(Map.of(
              "name", "elasticsearch-" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();

      case YUM -> new AnsibleAdHocYumCommand.Builder()
          .hostIp(context.node().getIp())
          .args(Map.of(
              "name", "elasticsearch-" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();

      case null, default -> throw new IllegalStateException(
          "Unsupported package manager " + context.node().getOs().packageManager()
      );
    };
    return runAdHocCommand(command);
  }
}