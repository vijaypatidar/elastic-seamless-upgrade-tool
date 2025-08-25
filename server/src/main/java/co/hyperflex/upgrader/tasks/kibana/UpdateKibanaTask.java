package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.ansible.commands.AnsibleAdHocAptCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocYumCommand;
import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class UpdateKibanaTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Update kibana service";
  }

  @Override
  public TaskResult run(Context context) {

    boolean isUbuntu = true; // This should be determined from the node's OS
    if (isUbuntu) {
      AnsibleAdHocAptCommand cmd = new AnsibleAdHocAptCommand
          .Builder()
          .hostIp(context.node().getIp())
          .args(Map.of(
              "name", "kibana=" + context.config().targetVersion(),
              "state", "present")
          )
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();
      return runAdHocCommand(cmd);
    } else {
      AnsibleAdHocYumCommand cmd = new AnsibleAdHocYumCommand
          .Builder()
          .hostIp(context.node().getIp())
          .args(Map.of(
              "name", "kibana-" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();
      return runAdHocCommand(cmd);
    }
  }
}
