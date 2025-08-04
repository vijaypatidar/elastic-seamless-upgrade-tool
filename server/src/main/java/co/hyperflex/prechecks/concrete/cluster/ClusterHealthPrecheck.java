package co.hyperflex.prechecks.concrete.cluster;

import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ClusterHealthPrecheck extends BaseClusterPrecheck {

  @Override
  public String getName() {
    return "Cluster health check";
  }

  @Override
  public void run(ClusterContext context) {
    Logger logger = context.getLogger();
    try {
      String status = context.getElasticClient().getHealthStatus();
      boolean isHealthy = "green".equalsIgnoreCase(status);

      String message = String.format("Cluster health status: '%s'. Expected: 'green'.", status);
      logger.info(message);

      if (!isHealthy) {
        logger.error("Cluster health check failed. {}", message);
        throw new RuntimeException();
      }
    } catch (Exception e) {
      logger.error("Failed to check cluster health", e);
      throw new RuntimeException();
    }
  }
}
