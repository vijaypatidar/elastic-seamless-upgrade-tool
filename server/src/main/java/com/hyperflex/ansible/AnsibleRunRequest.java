package com.hyperflex.ansible;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A record to encapsulate all parameters for a playbook run.
 * This makes the method signature clean and extensible.
 */
public record AnsibleRunRequest(
    // Unique identifier
    String id,

    // The path to the playbook file relative to the configured playbook directory.
    String playbookName,

    // A map representing the inventory. Keys are group names (e.g., "elasticsearch"),
    // values are lists of hostnames/IPs.
    Map<String, Object> inventory,

    // A map of extra variables to be passed to the playbook via --extra-vars.
    Map<String, Object> extraVars,

    // A consumer to stream log lines (stdout/stderr) back to the caller in real-time.
    Consumer<String> logConsumer,

    // Optional: Path to a private SSH key file.
    Path privateKeyFile,

    // Optional: A tag to run specific parts of a playbook.
    String tags
) {

  public static class Builder {
    private String id;
    private String playbookName;
    private Map<String, Object> inventory;
    private Map<String, Object> extraVars;
    private Consumer<String> logConsumer;
    private Path privateKeyFile;
    private String tags;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder playbookName(String playbookName) {
      this.playbookName = playbookName;
      return this;
    }

    public Builder inventory(Map<String, Object> inventory) {
      this.inventory = inventory;
      return this;
    }

    public Builder extraVars(Map<String, Object> extraVars) {
      this.extraVars = extraVars;
      return this;
    }

    public Builder logConsumer(Consumer<String> logConsumer) {
      this.logConsumer = logConsumer;
      return this;
    }

    public Builder privateKeyFile(Path privateKeyFile) {
      this.privateKeyFile = privateKeyFile;
      return this;
    }

    public Builder tags(String tags) {
      this.tags = tags;
      return this;
    }

    public AnsibleRunRequest build() {
      return new AnsibleRunRequest(id, playbookName, inventory, extraVars, logConsumer,
          privateKeyFile, tags);
    }
  }
}