package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.ansible.AnsibleAdHocCommand;
import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class UpdateKibanaTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {

    boolean isUbuntu = true; // This should be determined from the node's OS
    if (isUbuntu) {
      AnsibleAdHocCommand cmd = new AnsibleAdHocCommand
          .Builder()
          .hostIp(context.node().getIp())
          .module("ansible.builtin.apt")
          .args(Map.of(
              "name", "kibana=" + context.config().targetVersion(),
              "state", "present")
          )
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();
      return runAdHocCommand(cmd, context);
    } else {
      AnsibleAdHocCommand cmd = new AnsibleAdHocCommand
          .Builder()
          .hostIp(context.node().getIp())
          .module("ansible.builtin.yum")
          .args(Map.of(
              "name", "kibana-" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .sshUsername(context.config().sshUser())
          .sshKeyPath(context.config().sshKeyPath())
          .build();
      return runAdHocCommand(cmd, context);
    }
  }
}
