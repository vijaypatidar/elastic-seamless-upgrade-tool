package co.hyperflex.upgrade.tasks.kibana;

import co.hyperflex.upgrade.tasks.AbstractUpdatePluginTask;

public class UpdateKibanaPluginTask extends AbstractUpdatePluginTask {
  @Override
  public String getName() {
    return "Update kibana plugins";
  }
}
