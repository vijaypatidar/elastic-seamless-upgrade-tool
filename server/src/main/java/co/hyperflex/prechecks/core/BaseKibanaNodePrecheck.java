package co.hyperflex.prechecks.core;

import co.hyperflex.core.models.enums.ClusterNodeType;
import co.hyperflex.prechecks.contexts.NodeContext;

public abstract class BaseKibanaNodePrecheck extends BaseNodePrecheck {

  @Override
  public boolean shouldRun(NodeContext context) {
    return context.getNode().getType() == ClusterNodeType.KIBANA;
  }
}
