package co.hyperflex.entities.cluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClusterNodeType {
  ELASTIC("elastic"),
  KIBANA("kibana");

  private final String value;

  ClusterNodeType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ClusterNodeType fromValue(String value) {
    for (ClusterNodeType type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown ClusterNodeType: " + value);
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
