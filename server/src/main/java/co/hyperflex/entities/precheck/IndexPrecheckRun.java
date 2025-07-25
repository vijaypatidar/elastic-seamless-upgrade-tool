package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class IndexPrecheckRun extends PrecheckRun {
  private IndexInfo index;

  public IndexPrecheckRun() {
    setType(PrecheckType.INDEX);
  }

  public static class IndexInfo {
    private String name;

    public IndexInfo() {
    }

    public IndexInfo(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
    }
  }

  public IndexInfo getIndex() {
    return index;
  }

  public void setIndex(IndexInfo index) {
    this.index = index;
  }
}