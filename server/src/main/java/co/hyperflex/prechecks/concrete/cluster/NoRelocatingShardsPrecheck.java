package co.hyperflex.prechecks.concrete.cluster;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.shards.ShardsRecord;
import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import co.hyperflex.prechecks.core.PrecheckLogger;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NoRelocatingShardsPrecheck extends BaseClusterPrecheck {

  @Override
  public String getName() {
    return "No relocating shards";
  }

  @Override
  public void run(ClusterContext context) {
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    PrecheckLogger logger = context.getLogger();

    try {

      List<ShardsRecord> shards =
          client.cat().shards(s -> s.h("index", "shard", "state", "node")).valueBody();

      List<ShardsRecord> relocatingShards =
          shards.stream().filter(s -> "RELOCATING".equalsIgnoreCase(s.state())).toList();

      for (ShardsRecord shard : relocatingShards) {
        logger.error("Relocating shard: index=%s, shard=%s, from=%s", shard.index(), shard.shard(),
            shard.node());
      }

      if (!relocatingShards.isEmpty()) {
        throw new RuntimeException(
            String.format("Relocating shards check failed. %d shard(s) are currently relocating.",
                relocatingShards.size()));
      }

    } catch (IOException e) {
      logger.error("Failed to check relocating shards", e);
      throw new RuntimeException("Failed to check relocating shards", e);
    }
  }
}
