package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.upgrader.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrader.tasks.AnsibleAdHocCommand;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;

public class UpdateElasticsearchTask extends AbstractAnsibleTask {

  @Override
  public TaskResult run(Context context) {

    boolean isUbuntu = true; //
    if (isUbuntu) {
      AnsibleAdHocCommand cmd = new AnsibleAdHocCommand
          .Builder()
          .hostIp(context.node().getIp())
          .module("ansible.builtin.apt")
          .args(Map.of(
              "name", "elasticsearch=" + context.config().targetVersion(),
              "state", "present")
          )
          .useBecome(true)
          .build();
      return runAdHocCommand(cmd, context);
    } else {
      AnsibleAdHocCommand cmd = new AnsibleAdHocCommand
          .Builder()
          .hostIp(context.node().getIp())
          .module("ansible.builtin.yum")
          .args(Map.of(
              "name", "elasticsearch-" + context.config().targetVersion(),
              "state", "present"
          ))
          .useBecome(true)
          .build();
      return runAdHocCommand(cmd, context);
    }
  }
}