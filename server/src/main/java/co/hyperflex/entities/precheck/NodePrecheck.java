package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class NodePrecheck extends Precheck {
  private NodeInfo node;

  public static class NodeInfo {
    private String id;
    private String name;
    private String ip;
  }
}