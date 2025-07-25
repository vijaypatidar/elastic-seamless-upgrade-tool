package co.hyperflex.prechecks.core;

import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.prechecks.contexts.ClusterContext;

public abstract class BaseClusterPrecheck implements Precheck<ClusterContext> {

  @Override
  public PrecheckType getType() {
    return PrecheckType.CLUSTER;
  }
}
