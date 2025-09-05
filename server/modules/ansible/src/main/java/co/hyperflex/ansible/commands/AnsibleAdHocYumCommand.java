package co.hyperflex.ansible.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnsibleAdHocYumCommand extends AnsibleAdHocCommand {
  private final Map<String, Object> args;

  private AnsibleAdHocYumCommand(Builder builder) {
    this.args = builder.args;
  }

  @Override
  public String getModule() {
    return "ansible.builtin.yum";
  }

  @Override
  public List<String> getArguments() {
    if (getArgs() == null) {
      return Collections.emptyList();
    }
    return getArgs().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).toList();
  }

  public Map<String, Object> getArgs() {
    return args;
  }

  public static class Builder {
    private Map<String, Object> args;


    public Builder args(Map<String, Object> args) {
      this.args = args;
      return this;
    }

    public AnsibleAdHocYumCommand build() {
      return new AnsibleAdHocYumCommand(this);
    }
  }
}

