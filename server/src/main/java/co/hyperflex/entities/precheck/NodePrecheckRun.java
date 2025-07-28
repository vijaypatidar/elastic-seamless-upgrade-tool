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

  public static class NodeInfo {
    private String id;
    private String name;
    private String ip;

    public NodeInfo() {
    }

    public NodeInfo(String id, String name, String ip) {
      this.id = id;
      this.name = name;
      this.ip = ip;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getIp() {
      return ip;
    }

    public void setIp(String ip) {
      this.ip = ip;
    }
  }
}