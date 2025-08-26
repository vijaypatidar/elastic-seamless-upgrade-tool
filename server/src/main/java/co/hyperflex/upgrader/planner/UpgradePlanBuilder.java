package co.hyperflex.upgrader.planner;

import co.hyperflex.core.entites.clusters.nodes.ClusterNodeEntity;
import co.hyperflex.core.models.enums.ClusterNodeType;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.elastic.DisableShardAllocationTask;
import co.hyperflex.upgrader.tasks.elastic.EnableShardAllocationTask;
import co.hyperflex.upgrader.tasks.elastic.RestartElasticsearchServiceTask;
import co.hyperflex.upgrader.tasks.elastic.StartElasticsearchServiceTask;
import co.hyperflex.upgrader.tasks.elastic.StopElasticsearchServiceTask;
import co.hyperflex.upgrader.tasks.elastic.SyncedFlushTask;
import co.hyperflex.upgrader.tasks.elastic.UpdateElasticPluginTask;
import co.hyperflex.upgrader.tasks.elastic.UpdateElasticsearchTask;
import co.hyperflex.upgrader.tasks.elastic.WaitForElasticsearchHttpPortTask;
import co.hyperflex.upgrader.tasks.elastic.WaitForElasticsearchTransportPortTask;
import co.hyperflex.upgrader.tasks.elastic.WaitForGreenClusterStatusTask;
import co.hyperflex.upgrader.tasks.elastic.WaitForYellowOrGreenClusterStatusTask;
import co.hyperflex.upgrader.tasks.kibana.RestartKibanaServiceTask;
import co.hyperflex.upgrader.tasks.kibana.SetDefaultIndexTask;
import co.hyperflex.upgrader.tasks.kibana.UpdateKibanaTask;
import co.hyperflex.upgrader.tasks.kibana.WaitForKibanaPortTask;
import co.hyperflex.upgrader.tasks.kibana.WaitForKibanaReadyTask;
import java.util.ArrayList;
import java.util.List;

public class UpgradePlanBuilder {

  public List<Task> buildPlanFor(ClusterNodeEntity node) {
    List<Task> tasks = new ArrayList<>();

    if (node.getType() == ClusterNodeType.ELASTIC) {
      tasks.add(new StartElasticsearchServiceTask());
      tasks.add(new WaitForElasticsearchTransportPortTask());
      tasks.add(new WaitForGreenClusterStatusTask());
      tasks.add(new DisableShardAllocationTask());
      tasks.add(new SyncedFlushTask());
      tasks.add(new StopElasticsearchServiceTask());
      tasks.add(new UpdateElasticsearchTask());
      tasks.add(new UpdateElasticPluginTask());
      tasks.add(new RestartElasticsearchServiceTask());
      tasks.add(new WaitForElasticsearchTransportPortTask());
      tasks.add(new WaitForElasticsearchHttpPortTask());
      tasks.add(new WaitForYellowOrGreenClusterStatusTask());
      tasks.add(new EnableShardAllocationTask());
      tasks.add(new WaitForGreenClusterStatusTask());
    } else if (node.getType() == ClusterNodeType.KIBANA) {
      tasks.add(new UpdateKibanaTask());
      tasks.add(new RestartKibanaServiceTask());
      tasks.add(new WaitForKibanaPortTask());
      tasks.add(new WaitForKibanaReadyTask());
      tasks.add(new SetDefaultIndexTask());
    } else {
      throw new UnsupportedOperationException("Unsupported ClusterNodeType: " + node.getType());
    }

    return tasks;
  }

}
