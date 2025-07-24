package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.AnsibleAdHocCommand;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class RestartKibanaServiceTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {

    AnsibleAdHocCommand cmd = new AnsibleAdHocCommand
        .Builder()
        .hostIp(context.node().getIp())
        .module("ansible.builtin.systemd")
        .args(Map.of(
            "name", "kibana",
            "state", "restarted",
            "daemon_reload", "yes")
        )
        .useBecome(true)
        .build();
    return runAdHocCommand(cmd, context);
  }
}
