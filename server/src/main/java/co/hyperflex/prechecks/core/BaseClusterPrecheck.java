package co.hyperflex.prechecks.core;

import co.hyperflex.precheck.enums.PrecheckType;
import co.hyperflex.prechecks.contexts.ClusterContext;

public abstract non-sealed class BaseClusterPrecheck implements Precheck<ClusterContext> {

  @Override
  public final PrecheckType getType() {
    return PrecheckType.CLUSTER;
  }
}
