package co.hyperflex.prechecks.concrete.node.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.Ingest;
import co.elastic.clients.elasticsearch.nodes.NodesInfoResponse;
import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.Stats;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IngestLoadPrecheck extends BaseElasticNodePrecheck {

  private static final int THROUGHPUT_THRESHOLD = 1000; // Docs/sec

  @Override
  public String getName() {
    return "Ingest Node Load Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    PrecheckLogger logger = context.getLogger();

    try {
      NodesInfoResponse stats = client.nodes().info(r -> r.nodeId(nodeId));
      NodesStatsResponse statsResponse =
          client.nodes().stats(r -> r.nodeId(nodeId).metric("ingest"));

      Map<String, Stats> nodes = statsResponse.nodes();
      Stats nodeStats = nodes.get(nodeId);

      if (nodeStats == null) {
        throw new RuntimeException("Node with ID [" + nodeId + "] not found.");
      }

      String nodeName = nodeStats.name();

      var ingestTotal = Optional.ofNullable(nodeStats.ingest()).map(Ingest::total).orElse(null);

      if (ingestTotal == null || ingestTotal.count() == 0 || ingestTotal.timeInMillis() == 0) {
        logger.info("%s: Skipping ingest load check — no activity data.", nodeName);
        return;
      }

      double docsPerSec = ingestTotal.count() / (ingestTotal.timeInMillis() / 1000.0);
      String docsPerSecRounded = String.format("%.2f", docsPerSec);

      logger.info("%s: Ingested %d docs in %d ms → ~%s docs/sec", nodeName, ingestTotal.count(),
          ingestTotal.timeInMillis(), docsPerSecRounded);

      if (docsPerSec > THROUGHPUT_THRESHOLD) {
        logger.warn("%s: Ingest load is high (%s docs/sec). Threshold: %d docs/sec.", nodeName,
            docsPerSecRounded, THROUGHPUT_THRESHOLD);
      }

    } catch (IOException e) {
      logger.error("Failed to check ingest load for node: {}", nodeId, e);
      throw new RuntimeException("Failed to check ingest load for node: " + nodeId, e);
    }
  }
}
