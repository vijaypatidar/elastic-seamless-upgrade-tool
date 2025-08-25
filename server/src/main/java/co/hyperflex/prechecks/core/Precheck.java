package co.hyperflex.prechecks.core;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckType;
import co.hyperflex.prechecks.contexts.PrecheckContext;

public sealed interface Precheck<T extends PrecheckContext> permits BaseClusterPrecheck, BaseIndexPrecheck, BaseNodePrecheck {
  String getName();

  PrecheckType getType();

  void run(T context);

  default String getId() {
    return this.getClass().getName();
  }

  default PrecheckSeverity getSeverity() {
    return PrecheckSeverity.ERROR;
  }

  default boolean shouldRun(T context) {
    return true;
  }

}
