package co.hyperflex.entities.precheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PrecheckExecutionType {
  ANSIBLE("ansible"),
  JAVA("java");

  private final String value;

  PrecheckExecutionType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static PrecheckExecutionType fromValue(String value) {
    for (PrecheckExecutionType status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown PrecheckExecutionType: " + value);
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
