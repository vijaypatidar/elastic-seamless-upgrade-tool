package co.hyperflex.ansible.commands;

import java.util.List;

public abstract class AnsibleAdHocCommand {

  public abstract String getHostIp();

  public abstract String getModule();

  public abstract boolean isUseBecome();

  public abstract String getSshUser();

  public abstract String getSshKeyPath();

  public abstract List<String> getArguments();
}

