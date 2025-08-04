package co.hyperflex.entities.upgrade;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "upgrade-logs")
public class UpgradeLog {
  @Id
  private String id;
  private List<String> logs;
  private String clusterUpgradeJobId;
  private String nodeId;

  public UpgradeLog() {
  }

  public List<String> getLogs() {
    return logs;
  }

  public void setLogs(List<String> logs) {
    this.logs = logs;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClusterUpgradeJobId() {
    return clusterUpgradeJobId;
  }

  public void setClusterUpgradeJobId(String clusterUpgradeJobId) {
    this.clusterUpgradeJobId = clusterUpgradeJobId;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
}
