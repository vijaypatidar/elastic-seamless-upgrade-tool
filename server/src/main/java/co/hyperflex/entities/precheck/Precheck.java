package co.hyperflex.entities.precheck;

import co.hyperflex.entities.cluster.ClusterNode;

public class Precheck {

  private String id;

  private ClusterNode node;

  private PrecheckJob precheckJob;

  private PrecheckDefinition precheckDefinition;

  private JavaPrecheckRun javaPrecheckRun;

  private PrecheckStatus status;

}
