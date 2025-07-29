package co.hyperflex.ansible;

import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnsibleService {

  private static final Logger logger = LoggerFactory.getLogger(AnsibleService.class);

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
      throw new RuntimeException(e);
    }
  }

  private Process getProcess(AnsibleAdHocCommand cmd) throws IOException {
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

    command.add(cmd.getSshUser());
    command.add("--private-key");
    command.add(cmd.getSshKeyPath());
    command.add(
        "-e 'ansible_ssh_common_args=-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null'");
    command.add("-b");

    ProcessBuilder builder = new ProcessBuilder(command);
    return builder.start();
  }

}
