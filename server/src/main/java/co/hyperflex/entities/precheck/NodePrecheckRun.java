package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class NodePrecheckRun extends PrecheckRun {
  private NodeInfo node;

  public NodePrecheckRun() {
    setType(PrecheckType.NODE);
  }

  public NodeInfo getNode() {
    return node;
  }

  public void setNode(NodeInfo node) {
    this.node = node;
  }

  public record NodeInfo(String id, String name, String ip, int rank) {
  }
}