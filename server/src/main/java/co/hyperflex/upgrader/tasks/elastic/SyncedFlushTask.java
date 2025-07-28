package co.hyperflex.upgrader.tasks.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.io.IOException;

public class SyncedFlushTask implements Task {

  @Override
  public TaskResult run(Context context) {
    ElasticsearchClient elasticsearchClient = context.elasticClient().getElasticsearchClient();
    try {
      elasticsearchClient.indices().flush();
    } catch (IOException e) {
      context.logger().error("Failed to flush indices", e);
    }
    return TaskResult.success("Synced flush performed.");
  }
}
