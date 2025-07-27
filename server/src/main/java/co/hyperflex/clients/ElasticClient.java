package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.master.MasterRecord;
import co.elastic.clients.elasticsearch.cluster.GetClusterSettingsResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.json.JsonData;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ElasticClient {
  private final ElasticsearchClient elasticsearchClient;

  public ElasticClient(ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  public ElasticsearchClient getElasticsearchClient() {
    return elasticsearchClient;
  }

  public List<String> getIndices() {
    ElasticsearchIndicesClient indices = elasticsearchClient.indices();
    GetIndexRequest request = GetIndexRequest.of(b -> b.index("*"));

    try {
      GetIndexResponse response = indices.get(request);
      return response.result()
          .keySet()
          .stream()
          .filter(indexState -> !indexState.startsWith("."))
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<MasterRecord> getActiveMasters() throws IOException {
    return getElasticsearchClient().cat().master().valueBody();
  }

  public InfoResponse getClusterInfo() {
    try {
      return getElasticsearchClient().info();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Boolean isAdaptiveReplicaEnabled() throws IOException {
    GetClusterSettingsResponse settings =
        getElasticsearchClient().cluster().getSettings(b -> b.includeDefaults(true));

    Map<String, JsonData> transientSettings = settings.transient_();
    Map<String, JsonData> persistentSettings = settings.persistent();
    Map<String, JsonData> defaultSettings = settings.defaults();

    String value = null;

    if (transientSettings.containsKey("search.adaptive_replica_selection")) {
      value = transientSettings.get("search.adaptive_replica_selection").to(String.class);
    } else if (persistentSettings.containsKey("search.adaptive_replica_selection")) {
      value = persistentSettings.get("search.adaptive_replica_selection").to(String.class);
    } else if (defaultSettings.containsKey("search.adaptive_replica_selection")) {
      value = defaultSettings.get("search.adaptive_replica_selection").to(String.class);
    }

    return Boolean.parseBoolean(value);
  }
}
