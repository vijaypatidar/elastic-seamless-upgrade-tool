package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.AnsibleAdHocCommand;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class StartElasticsearchServiceTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocCommand cmd = new AnsibleAdHocCommand.Builder()
        .hostIp(context.node().getIp())
        .module("ansible.builtin.systemd")
        .args(Map.of("name", "elasticsearch", "state", "started"))
        .useBecome(true)
        .build();
    return runAdHocCommand(cmd, context);
  }
}