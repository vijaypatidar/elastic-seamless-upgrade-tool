package co.hyperflex.entities.upgrade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClusterNodeUpgradeStatus {
  PENDING("pending"),
  UPGRADING("upgrading"),
  UPDATED("updated"),
  FAILED("failed");

  private final String value;

  ClusterNodeUpgradeStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ClusterNodeUpgradeStatus fromValue(String value) {
    for (ClusterNodeUpgradeStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown ClusterNodeUpgradeStatus: " + value);
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
