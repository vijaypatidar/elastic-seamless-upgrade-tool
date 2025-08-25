package co.hyperflex.prechecks.core;

import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.prechecks.contexts.IndexContext;

public abstract non-sealed class BaseIndexPrecheck implements Precheck<IndexContext> {

  @Override
  public final PrecheckType getType() {
    return PrecheckType.INDEX;
  }
}
