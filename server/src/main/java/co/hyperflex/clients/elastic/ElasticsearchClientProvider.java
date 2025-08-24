package co.hyperflex.clients.elastic;

import co.hyperflex.entities.cluster.ClusterEntity;
import jakarta.validation.constraints.NotNull;

public interface ElasticsearchClientProvider {
  ElasticClient getClientByClusterId(@NotNull String clusterId);

  ElasticClient buildElasticClient(ClusterEntity cluster);
}
