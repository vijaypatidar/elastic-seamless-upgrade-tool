package co.hyperflex.prechecks.concrete.cluster;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.nodes.NodesRecord;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MasterEligibleNodesPrecheck extends BaseClusterPrecheck {

  @Override
  public String getName() {
    return "Minimum number of master-eligible nodes";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(ClusterContext context) {
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    Logger logger = context.getLogger();

    try {
      List<NodesRecord> nodes = client.cat().nodes().valueBody();

      int totalNodes = nodes.size();

      List<NodesRecord> masterEligibleNodes = nodes.stream()
          .filter(n -> n.nodeRole() != null && n.nodeRole().contains("m"))
          .toList();

      int masterCount = masterEligibleNodes.size();

      logger.info("Found {} master-eligible node(s) out of {} total node(s).", masterCount,
          totalNodes);

      boolean isSmallCluster = totalNodes <= 3;

      if (isSmallCluster) {
        logger.info(
            "Small cluster detected ({} total node{}). For production high availability,"
                + " it's recommended to have at least 3 master-eligible nodes.",
            totalNodes,
            totalNodes > 1 ? "s" : ""
        );
        return;
      }

      if (masterCount % 2 == 0) {
        logger.warn(
            "Even number ({}) of master-eligible nodes detected. Consider using an "
                + "odd number (e.g., {}) to avoid split-brain scenarios.",
            masterCount,
            masterCount + 1
        );
      }

      int quorum = (masterCount / 2) + 1;
      int toleratedFailures = masterCount - quorum;

      logger.info(
          "Master quorum requirement: {} out of {} master-eligible nodes."
              + " Cluster can tolerate up to {} failure(s) and still elect a master.",
          quorum,
          masterCount,
          toleratedFailures
      );

      if (masterCount < 3) {
        logger.error(
            "Only {} master-eligible node{} detected. This is below the recommended minimum of 3 for safe master elections.",
            masterCount,
            masterCount > 1 ? "s" : ""
        );
        logger.error(
            "Insufficient master-eligible nodes: found {} need at least 3 for high availability.",
            masterCount);
        throw new RuntimeException();
      }

    } catch (IOException e) {
      logger.error("Failed to fetch node information from cluster", e);
      throw new RuntimeException("Failed to fetch node information from cluster", e);
    }
  }
}
