package co.hyperflex.upgrade.tasks.common.repository;

import co.hyperflex.ansible.commands.AnsibleAdHocCommand;
import co.hyperflex.core.models.enums.PackageManager;
import co.hyperflex.upgrade.tasks.AbstractAnsibleTask;
import co.hyperflex.upgrade.tasks.Context;
import co.hyperflex.upgrade.tasks.TaskResult;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AddAptRepositoryTask extends AbstractAnsibleTask {

  @Override
  public String getName() {
    return "Setup elastic 8.x repository for apt package manager";
  }

  @Override
  public TaskResult run(Context context) {
    var packageManager = context.node().getOs().packageManager();

    var downloadKeyResult = downloadAndSetupGpgKey(context, packageManager);
    if (downloadKeyResult != null) {
      return downloadKeyResult;
    }

    var command = AnsibleAdHocCommand.builder()
        .aptRepository()
        .args(Map.of(
            "repo",
            "'deb [signed-by=/usr/share/keyrings/elasticsearch-keyring.gpg] https://artifacts.elastic.co/packages/8.x/apt stable main'",
            "state", "present",
            "filename", "elastic-8.x"
        ))
        .build();
    return runAdHocCommand(command, context);
  }

  private TaskResult downloadAndSetupGpgKey(Context context, PackageManager packageManager) {
    var downloadKey = AnsibleAdHocCommand.builder()
        .module("ansible.builtin.get_url")
        .args(Map.of(
            "url", "https://artifacts.elastic.co/GPG-KEY-elasticsearch",
            "dest", "/tmp/GPG-KEY-elasticsearch",
            "mode", "0644"
        ))
        .build();
    var downloadKeyResult = runAdHocCommand(downloadKey, context);
    if (!downloadKeyResult.success()) {
      return downloadKeyResult;
    }
    var convertKey = AnsibleAdHocCommand.builder()
        .module("ansible.builtin.command")
        .args(Map.of(
            "cmd", "gpg --batch --yes --dearmor -o /usr/share/keyrings/elasticsearch-keyring.gpg /tmp/GPG-KEY-elasticsearch"
        ))
        .build();
    var convertKeyResult = runAdHocCommand(convertKey, context);
    if (!convertKeyResult.success()) {
      return downloadKeyResult;
    }
    return null;
  }
}