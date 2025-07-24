package co.hyperflex.entities.precheck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "java_precheck_runs")
public class JavaPrecheckRun {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String className;

  private LocalDateTime startedAt;
  private LocalDateTime completedAt;

  @Enumerated(EnumType.STRING)
  private PrecheckStatus status;

  @Column(columnDefinition = "TEXT")
  private String output;

  @Column(columnDefinition = "TEXT")
  private String error;

}
