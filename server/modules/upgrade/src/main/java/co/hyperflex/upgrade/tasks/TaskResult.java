package co.hyperflex.upgrade.tasks;

public class TaskResult {
  private final boolean success;
  private final String message;

  public TaskResult(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public static TaskResult failure(String s) {
    return new TaskResult(false, s);
  }

  public static TaskResult success(String s) {
    return new TaskResult(true, s);

  }

  @Override
  public String toString() {
    return "TaskResult{" + "success=" + success + ", message='" + message + '\'' + '}';
  }

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return success;
  }
}
