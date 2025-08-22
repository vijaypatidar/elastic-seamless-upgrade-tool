package co.hyperflex.clients.elastic.dto.cluster;

public class PutClusterSettingsResponse {
  private final boolean acknowledged;

  public PutClusterSettingsResponse(boolean acknowledged) {
    this.acknowledged = acknowledged;
  }

  public boolean isAcknowledged() {
    return acknowledged;
  }
}