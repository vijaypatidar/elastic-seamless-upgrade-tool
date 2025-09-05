package co.hyperflex.upgrade.tasks.kibana;

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
public class UpdateKibanaTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Update kibana service";
  }

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocCommand command = switch (context.node().getOs().packageManager()) {
      case APT -> new AnsibleAdHocAptCommand
          .Builder()
          .args(Map.of(
              "name", "kibana=" + context.config().targetVersion(),
              "state", "present")
          )
          .build();
      case DNF -> new AnsibleAdHocDnfCommand
          .Builder()
          .args(Map.of(
              "name", "kibana-" + context.config().targetVersion(),
              "state", "present"
          ))
          .build();
      case YUM -> new AnsibleAdHocYumCommand
          .Builder()
          .args(Map.of(
              "name", "kibana-" + context.config().targetVersion(),
              "state", "present"
          ))
          .build();
      case null, default -> throw new IllegalStateException("Unsupported package manager " + context.node().getOs().packageManager());
    };
    return runAdHocCommand(command, context);
  }
}
