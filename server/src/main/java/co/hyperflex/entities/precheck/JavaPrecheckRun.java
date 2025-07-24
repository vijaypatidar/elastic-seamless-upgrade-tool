package co.hyperflex.entities.precheck;

import java.time.LocalDateTime;

public class JavaPrecheckRun {
  private String id;
  private String className;
  private LocalDateTime startedAt;
  private LocalDateTime completedAt;
  private PrecheckStatus status;
  private String output;
  private String error;
}
