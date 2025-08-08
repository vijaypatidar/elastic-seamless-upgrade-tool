package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class ClusterPrecheckRun extends PrecheckRun {
  public ClusterPrecheckRun() {
    setType(PrecheckType.CLUSTER);
  }
}