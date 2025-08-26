package co.hyperflex.upgrade.tasks.kibana;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;

public class StopKibanaServiceTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Stop kibana service";
  }

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
    return runAdHocCommand(cmd);
  }
}
