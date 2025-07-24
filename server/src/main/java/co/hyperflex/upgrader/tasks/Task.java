package co.hyperflex.upgrader.tasks;

public interface Task {
  default String getId() {
    return getClass().getName();
  }

  TaskResult run(Context context);
}
