package co.hyperflex.upgrader.tasks.elastic;

import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.dto.cluster.PutClusterSettingsResponse;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.HashMap;
import java.util.Map;

public class DisableShardAllocationTask implements Task {

  @Override
  public String getName() {
    return "Disable Shard Allocation";
  }

  @Override
  public TaskResult run(Context context) {
    ElasticClient elasticsearchClient = context.elasticClient();
    Map<String, Object> transientSettings = new HashMap<>();
    transientSettings.put("cluster.routing.allocation.enable", "primaries");
    Map<String, Object> clusterSettings = Map.of(
        "transient",
        transientSettings
    );

    PutClusterSettingsResponse response = elasticsearchClient.updateClusterSettings(clusterSettings);
    if (!response.isAcknowledged()) {
      throw new IllegalStateException("Disabling allocation failed");
    }
    return TaskResult.success("Shard allocation disabled");
  }
}
