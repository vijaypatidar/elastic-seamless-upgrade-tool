package co.hyperflex.ansible.commands;

import java.util.List;
import java.util.Map;

public class AnsibleAdHocYumCommand extends AnsibleAdHocCommand {
  private final String hostIp;
  private final Map<String, Object> args;
  private final boolean useBecome;
  private final String sshUser;
  private final String sshKeyPath;

  private AnsibleAdHocYumCommand(Builder builder) {
    this.hostIp = builder.hostIp;
    this.args = builder.args;
    this.useBecome = builder.useBecome;
    this.sshUser = builder.sshUser;
    this.sshKeyPath = builder.sshKeyPath;
  }

  @Override
  public String getHostIp() {
    return hostIp;
  }

  @Override
  public String getModule() {
    return "ansible.builtin.yum";
  }

  @Override
  public boolean isUseBecome() {
    return useBecome;
  }

  @Override
  public String getSshUser() {
    return sshUser;
  }

  @Override
  public String getSshKeyPath() {
    return sshKeyPath;
  }

  @Override
  public List<String> getArguments() {
    return List.of();
  }

  public Map<String, Object> getArgs() {
    return args;
  }

  public static class Builder {
    public String sshKeyPath;
    public String sshUser;
    private String hostIp;
    private String module;
    private Map<String, Object> args;
    private boolean useBecome = true;

    public Builder hostIp(String hostIp) {
      this.hostIp = hostIp;
      return this;
    }

    public Builder args(Map<String, Object> args) {
      this.args = args;
      return this;
    }

    public Builder useBecome(boolean useBecome) {
      this.useBecome = useBecome;
      return this;
    }

    public Builder sshUsername(String sshUsername) {
      this.sshUser = sshUsername;
      return this;
    }

    public Builder sshKeyPath(String sshKeyPath) {
      this.sshKeyPath = sshKeyPath;
      return this;
    }

    public AnsibleAdHocYumCommand build() {
      return new AnsibleAdHocYumCommand(this);
    }
  }
}

