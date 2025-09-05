package co.hyperflex.upgrade.tasks.elastic;

import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RestartElasticsearchServiceTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Restart Elasticsearch Service";
  }

  @Override
  public TaskResult run(Context context) {
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand.Builder()
        .args(Map.of(
            "name", "elasticsearch",
            "state", "restarted",
            "enabled", "yes",
            "daemon_reload", "yes"
        ))
        .build();
    return runAdHocCommand(cmd, context);
  }
}