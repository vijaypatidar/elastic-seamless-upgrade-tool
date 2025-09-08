package co.hyperflex.upgrade.tasks.common.repository;

import co.hyperflex.ansible.commands.AnsibleAdHocCommand;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AddYumRepositoryTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Setup elastic 8.x repository for yum package manager";
  }

  @Override
  public TaskResult run(Context context) {
    var command = AnsibleAdHocCommand.builder()
        .module("ansible.builtin.yum_repository")
        .args(Map.of(
            "name", "elasticsearch",
            "description", "Elasticsearch repository for 8.x packages",
            "baseurl", "https://artifacts.elastic.co/packages/8.x/yum",
            "gpgcheck", "1",
            "gpgkey", "https://artifacts.elastic.co/GPG-KEY-elasticsearch",
            "enabled", "1",
            "state", "present"
        ))
        .build();

    return runAdHocCommand(command, context);
  }

}