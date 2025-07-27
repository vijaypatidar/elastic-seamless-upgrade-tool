package co.hyperflex.upgrader.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractAnsibleTask implements Task {

  private static Process getProcess(AnsibleAdHocCommand cmd, Context context) throws IOException {
    String inventory = cmd.getHostIp() + ",";
    List<String> command = new ArrayList<>();
    command.add("ansible");
    command.add("all");
    command.add("-i");
    command.add(inventory);
    command.add("-m");
    command.add(cmd.getModule());
    if (cmd.getArgs() != null) {
      command.add("-a");
      String args =
          cmd.getArgs().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
              .collect(Collectors.joining(" "));
      command.add(args);
    }
    command.add("-u");

    Configuration config = context.config();
    command.add(config.sshUser());
    command.add("--private-key");
    command.add(config.sshKeyPath());
    command.add("-e 'ansible_ssh_common_args=-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null'");
    if (cmd.isUseBecome()) {
      command.add("-b");
    }

    ProcessBuilder builder = new ProcessBuilder(command);
    return builder.start();
  }

  protected TaskResult runAdHocCommand(AnsibleAdHocCommand cmd, Context context) {
    try {
      Process process = getProcess(cmd, context);

      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      StringBuilder output = new StringBuilder();
      String line;
      while ((line = stdOut.readLine()) != null) {
        output.append(line).append("\n");
      }

      while ((line = stdErr.readLine()) != null) {
        output.append(line).append("\n");
      }

      int exitCode = process.waitFor();
      if (exitCode == 0) {
        return TaskResult.success(output.toString());
      } else {
        return TaskResult.failure(output.toString());
      }

    } catch (Exception e) {
      return TaskResult.failure("Exception while running Ansible: " + e.getMessage());
    }
  }
}
