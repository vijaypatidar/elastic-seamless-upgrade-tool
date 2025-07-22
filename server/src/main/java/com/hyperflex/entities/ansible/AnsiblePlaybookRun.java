package com.hyperflex.entities.ansible;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ansible-playbook-runs")
public class AnsiblePlaybookRun {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String playbookName;

  @Enumerated(EnumType.STRING)
  private AnsiblePlaybookRunStatus status;

  private LocalDateTime startedAt;
  private LocalDateTime endedAt;

  @OneToMany(mappedBy = "playbookRun", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AnsibleLogEntry> logs = new ArrayList<>();

  @OneToOne(mappedBy = "playbookRun", cascade = CascadeType.ALL, orphanRemoval = true)
  private AnsibleRunResult result;

  public AnsiblePlaybookRun() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPlaybookName() {
    return playbookName;
  }

  public void setPlaybookName(String playbookName) {
    this.playbookName = playbookName;
  }

  public AnsiblePlaybookRunStatus getStatus() {
    return status;
  }

  public void setStatus(AnsiblePlaybookRunStatus status) {
    this.status = status;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }

  public List<AnsibleLogEntry> getLogs() {
    return logs;
  }

  public void setLogs(List<AnsibleLogEntry> logs) {
    this.logs = logs;
  }

  public AnsibleRunResult getResult() {
    return result;
  }

  public void setResult(AnsibleRunResult result) {
    this.result = result;
  }
}
