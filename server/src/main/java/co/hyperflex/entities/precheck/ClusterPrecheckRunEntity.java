package co.hyperflex.entities.precheck;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prechecks")
public class ClusterPrecheckRunEntity extends PrecheckRunEntity {
  public ClusterPrecheckRunEntity() {
    setType(PrecheckType.CLUSTER);
  }
}