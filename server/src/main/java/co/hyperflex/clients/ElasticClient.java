package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.master.MasterRecord;
import co.elastic.clients.elasticsearch.cluster.GetClusterSettingsResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.snapshot.GetRepositoryResponse;
import co.elastic.clients.elasticsearch.snapshot.GetSnapshotResponse;
import co.elastic.clients.elasticsearch.snapshot.SnapshotInfo;
import co.elastic.clients.json.JsonData;
import co.hyperflex.dtos.GetElasticsearchSnapshotResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticClient {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticClient.class);
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

  public List<GetElasticsearchSnapshotResponse> getValidSnapshots() {
    try {
      GetRepositoryResponse repositoriesResponse =
          getElasticsearchClient().snapshot().getRepository(r -> r);
      Set<String> repositories = repositoriesResponse.result().keySet();

      if (repositories.isEmpty()) {
        return Collections.emptyList();
      }

      long now = System.currentTimeMillis();
      long twentyFourHoursAgo = now - 24 * 60 * 60 * 1000;

      List<GetElasticsearchSnapshotResponse> allValidSnapshots = new LinkedList<>();

      for (String repository : repositories) {
        try {
          GetSnapshotResponse snapshotResponse = getElasticsearchClient().snapshot().get(req -> req
              .repository(repository)
              .snapshot(List.of("_all"))
          );

          List<SnapshotInfo> snapshots = snapshotResponse.snapshots();
          if (snapshots == null || snapshots.isEmpty()) {
            continue;
          }

          List<GetElasticsearchSnapshotResponse> validSnapshots = snapshots.stream()
              .filter(s -> {
                Long time = s.startTimeInMillis();
                return time != null && time >= twentyFourHoursAgo && time <= now;
              })
              .map(s -> new GetElasticsearchSnapshotResponse(
                  s.snapshot(),
                  new Date(s.startTimeInMillis())
              ))
              .toList();

          allValidSnapshots.addAll(validSnapshots);

        } catch (Exception e) {
          LOG.error("Error fetching snapshots for repository {}: {}", repository, e.getMessage(),
              e);
        }
      }

      if (allValidSnapshots.isEmpty()) {
        LOG.info("No valid snapshots found within the last 24 hours.");
      }

      return allValidSnapshots;

    } catch (Exception e) {
      LOG.error("Error checking snapshot details:", e);
      throw new RuntimeException("Failed to get valid snapshots", e);
    }
  }
}
