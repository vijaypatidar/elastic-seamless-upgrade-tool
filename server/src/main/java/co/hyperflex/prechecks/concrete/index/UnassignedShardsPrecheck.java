package co.hyperflex.prechecks.concrete.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.shards.ShardsRecord;
import co.elastic.clients.elasticsearch.cluster.AllocationExplainRequest;
import co.elastic.clients.elasticsearch.cluster.AllocationExplainResponse;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UnassignedShardsPrecheck extends BaseIndexPrecheck {

  @Override
  public String getName() {
    return "Unassigned or Initializing Shards Check";
  }

  @Override
  public void run(IndexContext context) {
    String indexName = context.getIndexName();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    Logger logger = context.getLogger();

    try {
      List<ShardsRecord> shardRecords = client.cat().shards(r -> r.index(indexName))
          .valueBody();

      List<ShardsRecord> unassignedShards = shardRecords.stream()
          .filter(s -> "UNASSIGNED".equalsIgnoreCase(s.state()))
          .toList();

      List<ShardsRecord> initializingShards = shardRecords.stream()
          .filter(s -> "INITIALIZING".equalsIgnoreCase(s.state()))
          .toList();

      if (!unassignedShards.isEmpty() || !initializingShards.isEmpty()) {
        logger.warn("Index [{}] has unassigned or initializing shards.", indexName);

        for (ShardsRecord shard : unassignedShards) {
          if (shard.shard() != null && !shard.shard().isEmpty()) {
            try {
              int shardId = Integer.parseInt(shard.shard());
              boolean isPrimary = "p".equalsIgnoreCase(shard.prirep());

              AllocationExplainResponse explain = client.cluster().allocationExplain(
                  AllocationExplainRequest.of(req -> req
                      .index(indexName)
                      .shard(shardId)
                      .primary(isPrimary)
                  )
              );

              String explanation = explain.allocateExplanation() != null
                  ? explain.allocateExplanation()
                  : "No details available";

              logger.info(
                  "Unassigned shard [{}] (primary: {}) explanation: {}",
                  shard.shard(), isPrimary, explanation
              );
            } catch (NumberFormatException e) {
              logger.warn("Skipping invalid shard number: [{}]", shard.shard());
            }
          }
        }

        throw new RuntimeException("Index has unassigned or initializing shards.");
      } else {
        logger.info("Index [{}] has all shards in assigned and started state.", indexName);
      }
    } catch (IOException e) {
      logger.error("Failed to check unassigned shards for index: {}", indexName, e);
      throw new RuntimeException("Failed to check unassigned shards for index: " + indexName, e);
    }
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }
}
