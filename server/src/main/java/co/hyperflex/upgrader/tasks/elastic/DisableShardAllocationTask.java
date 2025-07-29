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

public class DisableShardAllocationTask implements Task {

  @Override
  public TaskResult run(Context context) {
    ElasticsearchClient elasticsearchClient = context.elasticClient().getElasticsearchClient();

    Map<String, JsonData> settings = new HashMap<>();
    settings.put("cluster.routing.allocation.enable", JsonData.of("primaries"));

    PutClusterSettingsRequest request =
        new PutClusterSettingsRequest.Builder().transient_(settings).build();

    try {
      PutClusterSettingsResponse response = elasticsearchClient.cluster().putSettings(request);
      if (!response.acknowledged()) {
        throw new IllegalStateException("Disabling allocation failed");
      }
      return TaskResult.success("Shard allocation disabled");
    } catch (IOException e) {
      context.logger().error("Failed to disable shard allocation", e);
      throw new RuntimeException(e);
    }
  }
}
