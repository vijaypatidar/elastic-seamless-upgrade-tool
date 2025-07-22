package com.hyperflex.entities.precheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PrecheckType {
  CLUSTER("cluster"),
  NODE("node");

  private final String value;

  PrecheckType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static PrecheckType fromValue(String value) {
    for (PrecheckType status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown PrecheckType: " + value);
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
