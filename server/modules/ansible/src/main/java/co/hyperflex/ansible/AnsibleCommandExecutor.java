package co.hyperflex.ansible;

import co.hyperflex.ansible.commands.AnsibleAdHocCommand;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleCommandExecutor {

  private static final Logger logger = LoggerFactory.getLogger(AnsibleCommandExecutor.class);

  public int run(@NotNull AnsibleAdHocCommand cmd,
                 @NotNull Consumer<String> stdLogsConsumer,
                 @NotNull Consumer<String> errLogsConsumer) {
    try {
      Process process = getProcess(cmd);

      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      String line;
      while ((line = stdOut.readLine()) != null) {
        stdLogsConsumer.accept(line);
      }

      while ((line = stdErr.readLine()) != null) {
        errLogsConsumer.accept(line);
      }

      return process.waitFor();
    } catch (Exception e) {
      logger.error("Failed to run ansible command", e);
      throw new AnsibleExecutionException("Failed to run ansible command", e);
    }
  }

  public AnsibleAdHocCommandResult run(@NotNull AnsibleAdHocCommand cmd) {
    List<String> stdOutLogs = new LinkedList<>();
    List<String> strErrLogs = new LinkedList<>();

    int success = run(cmd, stdOutLogs::add, strErrLogs::add);

    return new AnsibleAdHocCommandResult(success == 0, stdOutLogs, strErrLogs);
  }

  protected Process getProcess(AnsibleAdHocCommand cmd) throws IOException {
    String inventory = cmd.getHostIp() + ",";
    List<String> command = new ArrayList<>();
    command.add("ansible");
    command.add("all");
    command.add("-i");
    command.add(inventory);
    command.add("-m");
    command.add(cmd.getModule());

    List<String> args = cmd.getArguments();
    if (!args.isEmpty()) {
      command.add("-a");
      command.add(String.join(" ", args));
    }

    command.add("-u");
    command.add(cmd.getSshUser());
    command.add("--private-key");
    command.add(cmd.getSshKeyPath());
    command.add("-e");
    command.add("ansible_ssh_common_args='-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null'");
    command.add("-b");

    ProcessBuilder builder = new ProcessBuilder(command);
    return builder.start();
  }

}
