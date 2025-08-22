package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class StartKibanaServiceTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Start kibana service";
  }

  @Override
  public TaskResult run(Context context) {
    context.logger().info("Starting Kibana service task");
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand
        .Builder()
        .hostIp(context.node().getIp())
        .args(Map.of(
            "name", "kibana",
            "state", "started",
            "daemon_reload", "yes")
        )
        .sshUsername(context.config().sshUser())
        .sshKeyPath(context.config().sshKeyPath())
        .useBecome(true)
        .build();
    return runAdHocCommand(cmd);
  }
}
