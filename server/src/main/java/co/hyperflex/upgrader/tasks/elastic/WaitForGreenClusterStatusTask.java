package co.hyperflex.upgrader.tasks.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.HealthResponse;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.io.IOException;
import org.slf4j.Logger;

public class WaitForGreenClusterStatusTask implements Task {

  private static final int MAX_RETRIES = 30;
  private static final int RETRY_DELAY_MILLIS = 2000;

  @Override
  public TaskResult run(Context context) {
    final ElasticsearchClient client = context.elasticClient().getElasticsearchClient();
    final Logger logger = context.logger();

    for (int i = 0; i < MAX_RETRIES; i++) {
      try {
        HealthResponse health = client.cat().health();
        if (health.valueBody().stream().map(hr -> "green".equals(hr.status()))
            .reduce(Boolean::logicalAnd).orElse(false)) {
          logger.info("Cluster health is green");
          return new TaskResult(true, "Cluster health is green.");
        }
        logger.warn("Attempt {}/{}: Cluster health is not green. Retrying in {}ms...", i + 1,
            MAX_RETRIES, RETRY_DELAY_MILLIS);
        Thread.sleep(RETRY_DELAY_MILLIS);
      } catch (InterruptedException | IOException e) {
        throw new RuntimeException(e);
      }
    }
    logger.error("Cluster health did not become green after {} attempts.", MAX_RETRIES);
    return TaskResult.failure("Cluster health did not become green after multiple retries.");
  }
}
