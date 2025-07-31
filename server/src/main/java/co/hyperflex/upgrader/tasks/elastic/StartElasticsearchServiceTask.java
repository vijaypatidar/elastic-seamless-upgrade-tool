package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.ansible.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class StartElasticsearchServiceTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand.Builder()
        .hostIp(context.node().getIp())
        .args(Map.of("name", "elasticsearch", "state", "started"))
        .useBecome(true)
        .sshUsername(context.config().sshUser())
        .sshKeyPath(context.config().sshKeyPath())
        .build();
    return runAdHocCommand(cmd, context);
  }
}