package co.hyperflex.ansible.commands;

import java.util.List;

public abstract class AnsibleAdHocCommand {

  public abstract String getModule();

  public abstract List<String> getArguments();
}

