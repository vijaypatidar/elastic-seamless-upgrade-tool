package co.hyperflex.entities.precheck;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "precheck_definitions")
public class PrecheckDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name; // e.g., "Disk Space Check"
  private String description;

  @Enumerated(EnumType.STRING)
  private PrecheckType type;

  @Enumerated(EnumType.STRING)
  private PrecheckExecutionType executionType;


}
