package co.hyperflex.clients.elastic;

import co.hyperflex.entities.cluster.ClusterEntity;
import jakarta.validation.constraints.NotNull;

public interface ElasticsearchClientProvider {
  ElasticClient getClient(@NotNull String clusterId);

  ElasticClient getClient(ClusterEntity cluster);
}
