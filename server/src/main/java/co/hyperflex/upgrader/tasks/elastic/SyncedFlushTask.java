package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;

public class SyncedFlushTask implements Task {

  @Override
  public String getName() {
    return "Flush data streams or indices";
  }

  @Override
  public TaskResult run(Context context) {
    context.elasticClient().flushIndices();
    return TaskResult.success("Synced flush performed.");
  }
}
