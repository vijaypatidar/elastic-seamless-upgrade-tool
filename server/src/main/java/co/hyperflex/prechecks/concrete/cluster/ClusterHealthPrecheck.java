package co.hyperflex.prechecks.concrete.cluster;

import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import org.springframework.stereotype.Component;

@Component
public class ClusterHealthPrecheck extends BaseClusterPrecheck {

  @Override
  public String getName() {
    return "Cluster health check";
  }

  @Override
  public void run(ClusterContext context) {
    PrecheckLogger logger = context.getLogger();
    try {
      String status = context.getElasticClient().getHealthStatus();
      boolean isHealthy = "green".equalsIgnoreCase(status);

      String message = String.format("Cluster health status: '%s'. Expected: 'green'.", status);
      logger.info(message);

      if (!isHealthy) {
        logger.error("Cluster health check failed. " + message);
        throw new RuntimeException("Cluster health check failed. " + message);
      }
    } catch (Exception e) {
      logger.error("Failed to check cluster health", e);
      throw new RuntimeException("Failed to check cluster health", e);
    }
  }
}
