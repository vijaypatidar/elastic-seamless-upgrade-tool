package co.hyperflex.upgrade.tasks.elastic;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;

public class StartElasticsearchServiceTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Start Elasticsearch Service";
  }

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand.Builder()
        .hostIp(context.node().getIp())
        .args(Map.of("name", "elasticsearch", "state", "started"))
        .useBecome(true)
        .sshUsername(context.config().sshUser())
        .sshKeyPath(context.config().sshKeyPath())
        .build();
    return runAdHocCommand(cmd);
  }
}