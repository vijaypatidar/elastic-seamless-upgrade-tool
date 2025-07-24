package co.hyperflex.entities.precheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PrecheckStatus {
  PENDING("pending"),
  RUNNING("running"),
  PASSED("passed"),
  FAILED("failed");

  private final String value;

  PrecheckStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static PrecheckStatus fromValue(String value) {
    for (PrecheckStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown PrecheckStatus: " + value);
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

