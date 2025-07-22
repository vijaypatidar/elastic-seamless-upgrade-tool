package com.hyperflex.entities.precheck;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "precheck_jobs")
public class PrecheckJob {

  private final LocalDateTime createdAt = LocalDateTime.now();
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

}
