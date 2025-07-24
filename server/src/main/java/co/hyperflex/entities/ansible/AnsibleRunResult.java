package co.hyperflex.entities.ansible;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ansible_playbook_run_results")
public class AnsibleRunResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "playbook_run_id", nullable = false)
  private AnsiblePlaybookRun playbookRun;

  private int returnCode;

  @Column(columnDefinition = "TEXT")
  private String stdout;

  @Column(columnDefinition = "TEXT")
  private String stderr;

  private String summary; // optional

}
