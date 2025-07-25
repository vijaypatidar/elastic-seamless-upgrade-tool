package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class IndexPrecheck extends Precheck {
  private IndexInfo index;

  public static class IndexInfo {
    private String name;
  }
}