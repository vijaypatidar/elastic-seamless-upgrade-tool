package co.hyperflex.prechecks.core;

import co.hyperflex.precheck.enums.PrecheckType;
import co.hyperflex.prechecks.contexts.IndexContext;

public abstract non-sealed class BaseIndexPrecheck implements Precheck<IndexContext> {

  @Override
  public final PrecheckType getType() {
    return PrecheckType.INDEX;
  }
}
