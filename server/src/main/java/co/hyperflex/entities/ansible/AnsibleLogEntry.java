package co.hyperflex.entities.ansible;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ansible_log_entries")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AnsibleLogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ansible_log_entry_seq")
  @SequenceGenerator(
      name = "ansible_log_entry_seq",
      sequenceName = "ansible_log_entry_sequence",
      allocationSize = 1
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playbook_run_id", nullable = false)
  private AnsiblePlaybookRun playbookRun;

  private LocalDateTime timestamp;

  @Column(columnDefinition = "TEXT")
  private String message;

  public AnsibleLogEntry() {
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AnsiblePlaybookRun getPlaybookRun() {
    return playbookRun;
  }

  public void setPlaybookRun(AnsiblePlaybookRun playbookRun) {
    this.playbookRun = playbookRun;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

