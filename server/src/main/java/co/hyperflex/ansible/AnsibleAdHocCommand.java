package co.hyperflex.ansible;

public abstract class AnsibleAdHocCommand {

  public abstract String getHostIp();

  public abstract String getModule();

  public abstract boolean isUseBecome();

  public abstract String getSshUser();

  public abstract String getSshKeyPath();

}

