package co.hyperflex.ansible.commands;

import java.util.Collections;
import java.util.List;

public class AnsibleAdHocShellCommand extends AnsibleAdHocCommand {
  private final String hostIp;
  private final String args;
  private final boolean useBecome;
  private final String sshUser;
  private final String sshKeyPath;

  private AnsibleAdHocShellCommand(Builder builder) {
    this.hostIp = builder.hostIp;
    this.args = builder.args;
    this.useBecome = builder.useBecome;
    this.sshUser = builder.sshUser;
    this.sshKeyPath = builder.sshKeyPath;
  }

  public String getHostIp() {
    return hostIp;
  }

  public String getModule() {
    return "ansible.builtin.shell";
  }

  public String getArgs() {
    return args;
  }

  public boolean isUseBecome() {
    return useBecome;
  }

  public String getSshUser() {
    return sshUser;
  }

  public String getSshKeyPath() {
    return sshKeyPath;
  }

  @Override
  public List<String> getArguments() {
    if (getArgs() == null) {
      return Collections.emptyList();
    }
    return List.of(getArgs());
  }

  public static class Builder {
    public String sshKeyPath;
    public String sshUser;
    private String hostIp;
    private String module;
    private String args;
    private boolean useBecome = true;

    public Builder hostIp(String hostIp) {
      this.hostIp = hostIp;
      return this;
    }

    public Builder args(String args) {
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

    public AnsibleAdHocShellCommand build() {
      return new AnsibleAdHocShellCommand(this);
    }
  }
}

