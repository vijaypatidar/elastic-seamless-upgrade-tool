package co.hyperflex.upgrade.tasks.kibana;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;

public class RestartKibanaServiceTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Restart kibana service";
  }

  @Override
  public TaskResult run(Context context) {

    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand
        .Builder()
        .hostIp(context.node().getIp())
        .args(Map.of(
            "name", "kibana",
            "state", "restarted",
            "daemon_reload", "yes")
        )
        .sshUsername(context.config().sshUser())
        .sshKeyPath(context.config().sshKeyPath())
        .useBecome(true)
        .build();
    return runAdHocCommand(cmd);
  }
}
