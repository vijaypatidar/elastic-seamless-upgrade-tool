package co.hyperflex.upgrader.tasks.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.PutClusterSettingsRequest;
import co.elastic.clients.elasticsearch.cluster.PutClusterSettingsResponse;
import co.elastic.clients.json.JsonData;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnableShardAllocationTask implements Task {

  @Override
  public String getName() {
    return "Enable Shard Allocation";
  }

  @Override
  public TaskResult run(Context context) {
    ElasticsearchClient elasticsearchClient = context.elasticClient().getElasticsearchClient();

    Map<String, JsonData> settings = new HashMap<>();
    settings.put("cluster.routing.allocation.enable", JsonData.of("all"));
    settings.put("cluster.routing.allocation.node_concurrent_recoveries", JsonData.of(5));
    settings.put("indices.recovery.max_bytes_per_sec", JsonData.of("500mb"));

    PutClusterSettingsRequest request =
        new PutClusterSettingsRequest.Builder().transient_(settings).build();

    try {
      PutClusterSettingsResponse response = elasticsearchClient.cluster().putSettings(request);
      if (!response.acknowledged()) {
        throw new IllegalStateException("Disabling allocation failed");
      }
      return TaskResult.success("Shard allocation disabled");
    } catch (IOException e) {
      context.logger().error("Failed to enable shard allocation", e);
      throw new RuntimeException(e);
    }
  }
}
