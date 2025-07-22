package com.hyperflex.entities.ansible;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AnsiblePlaybookRunStatus {
  PENDING("pending"),
  RUNNING("running"),
  SUCCESS("success"),
  FAILED("failed"),
  CANCELLED("cancelled");

  private final String value;

  AnsiblePlaybookRunStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static AnsiblePlaybookRunStatus fromValue(String value) {
    for (AnsiblePlaybookRunStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown AnsiblePlaybookRunStatus: " + value);
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
