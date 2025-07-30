package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.master.MasterRecord;
import co.elastic.clients.elasticsearch.cluster.ClusterStatsResponse;
import co.elastic.clients.elasticsearch.cluster.GetClusterSettingsResponse;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.cluster.stats.ClusterNodeCount;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.snapshot.GetRepositoryResponse;
import co.elastic.clients.elasticsearch.snapshot.GetSnapshotResponse;
import co.elastic.clients.elasticsearch.snapshot.SnapshotInfo;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ApiTypeHelper;
import co.hyperflex.dtos.GetElasticNodeAndIndexCountsResponse;
import co.hyperflex.dtos.GetElasticsearchSnapshotResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticClient {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticClient.class);
  private final ElasticsearchClient elasticsearchClient;

  public ElasticClient(ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  public ElasticsearchClient getElasticsearchClient() {
    ApiTypeHelper.DANGEROUS_disableRequiredPropertiesCheck(true);
    return elasticsearchClient;
  }

  public List<String> getIndices() {
    ElasticsearchIndicesClient indices = elasticsearchClient.indices();
    GetIndexRequest request = GetIndexRequest.of(b -> b.index("*"));

    try {
      GetIndexResponse response = indices.get(request);
      return response.result().keySet().stream().filter(indexState -> !indexState.startsWith("."))
          .toList();
    } catch (IOException e) {
      LOG.error("Failed to get indices", e);
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
      LOG.error("Failed to get cluster info", e);
      throw new RuntimeException(e);
    }
  }

  public Boolean isAdaptiveReplicaEnabled() throws IOException {
    try {
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
    } catch (IOException e) {
      LOG.error("Failed to get adaptive replica settings", e);
      throw e;
    }
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

      List<GetElasticsearchSnapshotResponse> allValidSnapshots =
          repositories.stream().parallel().flatMap(repository -> {
            try {
              GetSnapshotResponse snapshotResponse = getElasticsearchClient().snapshot()
                  .get(req -> req.repository(repository).snapshot(List.of("_all")));

              List<SnapshotInfo> snapshots = snapshotResponse.snapshots();
              return Optional.ofNullable(snapshots).stream().flatMap(Collection::stream);
            } catch (Exception e) {
              LOG.error("Error fetching snapshots for repository {}: {}", repository,
                  e.getMessage(),
                  e);
              return Stream.empty();
            }
          }).filter(s -> {
            Long time = s.startTimeInMillis();
            return time != null && time >= twentyFourHoursAgo && time <= now;
          }).map(s -> new GetElasticsearchSnapshotResponse(s.snapshot(),
              new Date(s.startTimeInMillis()))).toList();


      if (allValidSnapshots.isEmpty()) {
        LOG.info("No valid snapshots found within the last 24 hours.");
      }

      return allValidSnapshots;

    } catch (Exception e) {
      LOG.error("Error checking snapshot details:", e);
      throw new RuntimeException("Failed to get valid snapshots", e);
    }
  }

  public GetElasticNodeAndIndexCountsResponse getEntitiesCounts() {
    try {
      HealthResponse health = getElasticsearchClient().cluster().health();
      int totalIndices = health.indices().size();
      int activePrimaryShards = health.activePrimaryShards();
      int activeShards = health.activeShards();
      int unassignedShards = health.unassignedShards();
      int initializingShards = health.initializingShards();
      int relocatingShards = health.relocatingShards();
      ClusterStatsResponse stats = getElasticsearchClient().cluster().stats();
      ClusterNodeCount nodeCount = stats.nodes().count();
      int totalNodes = nodeCount.total();
      int dataNodes = nodeCount.data()
          + nodeCount.dataCold()
          + nodeCount.dataContent()
          + nodeCount.dataHot()
          + nodeCount.dataWarm();
      int masterNodes = nodeCount.master();

      return new GetElasticNodeAndIndexCountsResponse(dataNodes, totalNodes, masterNodes,
          totalIndices, activePrimaryShards, activeShards, unassignedShards, initializingShards,
          relocatingShards);
    } catch (IOException e) {
      LOG.error("Failed to get cluster stats", e);
      throw new RuntimeException("Failed to get cluster stats", e);
    }
  }
}
