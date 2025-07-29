package co.hyperflex.ansible;

import java.util.Map;

public class AnsibleAdHocCommand {
  private final String hostIp;
  private final String module;
  private final Map<String, Object> args;
  private final boolean useBecome;
  private final String sshUser;
  private final String sshKeyPath;

  private AnsibleAdHocCommand(Builder builder) {
    this.hostIp = builder.hostIp;
    this.module = builder.module;
    this.args = builder.args;
    this.useBecome = builder.useBecome;
    this.sshUser = builder.sshUser;
    this.sshKeyPath = builder.sshKeyPath;
  }

  public String getHostIp() {
    return hostIp;
  }


  public String getModule() {
    return module;
  }

  public Map<String, Object> getArgs() {
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

    public Builder module(String module) {
      this.module = module;
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

    public AnsibleAdHocCommand build() {
      return new AnsibleAdHocCommand(this);
    }
  }
}

