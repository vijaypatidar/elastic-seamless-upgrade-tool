package co.hyperflex.ansible.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnsibleAdHocAptCommand extends AnsibleAdHocCommand {
  private final Map<String, Object> args;

  private AnsibleAdHocAptCommand(Builder builder) {
    this.args = builder.args;
  }

  public String getModule() {
    return "ansible.builtin.apt";
  }

  public Map<String, Object> getArgs() {
    return args;
  }

  @Override
  public List<String> getArguments() {
    if (getArgs() == null) {
      return Collections.emptyList();
    }
    return getArgs().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).toList();
  }

  public static class Builder {
    private Map<String, Object> args;

    public Builder args(Map<String, Object> args) {
      this.args = args;
      return this;
    }

    public AnsibleAdHocAptCommand build() {
      return new AnsibleAdHocAptCommand(this);
    }
  }
}

