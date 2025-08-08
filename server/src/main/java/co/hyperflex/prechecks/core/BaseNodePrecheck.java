package co.hyperflex.prechecks.core;

import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.prechecks.contexts.NodeContext;

public abstract class BaseNodePrecheck implements Precheck<NodeContext> {

  @Override
  public final PrecheckType getType() {
    return PrecheckType.NODE;
  }
}
