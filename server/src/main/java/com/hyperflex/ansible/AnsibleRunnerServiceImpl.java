package com.hyperflex.ansible;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnsibleRunnerServiceImpl implements AnsibleRunnerService {

  private static final Logger log = LoggerFactory.getLogger(AnsibleRunnerServiceImpl.class);

  private final String ansiblePlaybookCommand;
  private final String playbooksDirectory;
  private final ObjectMapper objectMapper;
  private final Executor taskExecutor;
  private final Executor playBookExecutor;
  private final AnsibleLoggingService ansibleLoggingService;

  public AnsibleRunnerServiceImpl(
      @Value("${app.ansible.command:ansible-playbook}") String ansiblePlaybookCommand,
      @Value("${app.ansible.playbooks-directory}") String playbooksDirectory,
      ObjectMapper objectMapper,
      @Qualifier("ansibleTaskExecutor") Executor taskExecutor,
      @Qualifier("ansiblePlayBookExecutor") Executor playBookExecutor,
      AnsibleLoggingService ansibleLoggingService) {
    this.ansiblePlaybookCommand = ansiblePlaybookCommand;
    this.playbooksDirectory = playbooksDirectory;
    this.objectMapper = objectMapper;
    this.taskExecutor = taskExecutor;
    this.playBookExecutor = playBookExecutor;
    this.ansibleLoggingService = ansibleLoggingService;
  }

  @Override
  public CompletableFuture<Integer> executePlaybook(AnsibleRunRequest request) {
    return CompletableFuture.supplyAsync(() -> {
      Path tempInventoryFile = null;
      try {
        // 1. Create a temporary inventory file
        tempInventoryFile = generateInventoryFile(request.inventory());

        // 2. Build the command list to prevent command injection
        List<String> command = buildCommand(request, tempInventoryFile);
        log.info("Executing Ansible command: {}", String.join(" ", command));

        // 3. Start the process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(playbooksDirectory));
        Process process = processBuilder.start();

        // 4. Consume stdout and stderr
        consumeStream(process.getInputStream(), request);
        consumeStream(process.getErrorStream(), request);

        // 5. Wait for process to complete
        int exitCode = process.waitFor();
        log.info("Playbook '{}' finished with exit code: {}", request.playbookName(), exitCode);
        return exitCode;

      } catch (IOException | InterruptedException e) {
        log.error("Failed to execute Ansible playbook: {}", request.playbookName(), e);
        Thread.currentThread().interrupt(); // restore interrupt status
        throw new CompletionException(e);
      } finally {
        if (tempInventoryFile != null) {
          try {
            Files.deleteIfExists(tempInventoryFile);
            log.debug("Temporary inventory file deleted: {}", tempInventoryFile);
          } catch (IOException e) {
            log.error("Failed to delete temporary inventory file: {}", tempInventoryFile, e);
          }
        }
      }
    }, playBookExecutor);
  }

  private List<String> buildCommand(AnsibleRunRequest request, Path inventoryPath)
      throws JsonProcessingException {
    List<String> command = new ArrayList<>();
    command.add(ansiblePlaybookCommand);
    command.add("-i");
    command.add(inventoryPath.toAbsolutePath().toString());

    if (request.extraVars() != null && !request.extraVars().isEmpty()) {
      command.add("--extra-vars");
      command.add(objectMapper.writeValueAsString(request.extraVars()));
    }

    if (request.privateKeyFile() != null) {
      command.add("--private-key");
      command.add(request.privateKeyFile().toAbsolutePath().toString());
    }

    if (request.tags() != null && !request.tags().isBlank()) {
      command.add("--tags");
      command.add(request.tags());
    }

    // The playbook name is the final argument
    command.add(request.playbookName());
    return command;
  }

  private Path generateInventoryFile(Map<String, Object> inventory) throws IOException {
    Path tempFile = Files.createTempFile("ansible-inventory-", ".ini");
    try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
      for (Map.Entry<String, Object> entry : inventory.entrySet()) {
        writer.write("[" + entry.getKey() + "]");
        writer.newLine();
        if (entry.getValue() instanceof List<?> hostList) {
          for (Object host : hostList) {
            writer.write(host.toString());
            writer.newLine();
          }
        }
        writer.newLine();
      }
    }
    log.debug("Generated inventory file at: {}", tempFile);
    return tempFile;
  }

  private void consumeStream(InputStream inputStream, AnsibleRunRequest request) {
    taskExecutor.execute(
        () -> new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> {
          if (request.logConsumer() != null) {
            request.logConsumer().accept(line);
          }
          ansibleLoggingService.logEvent(new AnsibleLogEvent(request.id(), line));
        }));
  }
}
