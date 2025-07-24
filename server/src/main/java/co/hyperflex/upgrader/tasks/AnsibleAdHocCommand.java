package co.hyperflex.upgrader.tasks;

import java.util.Map;

public class AnsibleAdHocCommand {
  private final String hostIp;
  private final String module;
  private final Map<String, Object> args;
  private final boolean useBecome;
  private final String elkVersion;

  private AnsibleAdHocCommand(Builder builder) {
    this.hostIp = builder.hostIp;

    this.module = builder.module;
    this.args = builder.args;
    this.useBecome = builder.useBecome;
    this.elkVersion = builder.elkVersion;
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

  public String getElkVersion() {
    return elkVersion;
  }

  public static class Builder {
    private String hostIp;
    private String module;
    private Map<String, Object> args;
    private boolean useBecome;
    private String elkVersion;

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

    public Builder elkVersion(String elkVersion) {
      this.elkVersion = elkVersion;
      return this;
    }

    public AnsibleAdHocCommand build() {
      return new AnsibleAdHocCommand(this);
    }
  }
}

