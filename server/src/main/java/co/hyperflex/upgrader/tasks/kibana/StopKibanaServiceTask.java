package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class StopKibanaServiceTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {
    context.logger().info("Stopping Kibana service task");
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand
        .Builder()
        .hostIp(context.node().getIp())
        .args(Map.of(
            "name", "kibana",
            "state", "stopped",
            "daemon_reload", "yes")
        )
        .useBecome(true)
        .build();
    return runAdHocCommand(cmd, context);
  }
}
