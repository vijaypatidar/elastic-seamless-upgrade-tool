package co.hyperflex.prechecks.core;

public interface PrecheckLogger {
  void info(String message, Object... args);

  void warn(String message, Object... args);

  void error(String message, Object... args);
}