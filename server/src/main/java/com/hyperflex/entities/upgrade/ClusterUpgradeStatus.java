package com.hyperflex.entities.upgrade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClusterUpgradeStatus {
  PENDING("pending"),
  UPGRADING("upgrading"),
  UPDATED("updated"),
  FAILED("failed");

  private final String value;

  ClusterUpgradeStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ClusterUpgradeStatus fromValue(String value) {
    for (ClusterUpgradeStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown ClusterUpgradeStatus: " + value);
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
