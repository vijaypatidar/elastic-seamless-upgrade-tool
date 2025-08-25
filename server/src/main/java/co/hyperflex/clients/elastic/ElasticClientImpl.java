package co.hyperflex.clients.elastic;

import co.hyperflex.clients.elastic.dto.GetAllocationExplanationResponse;
import co.hyperflex.clients.elastic.dto.GetElasticDeprecationResponse;
import co.hyperflex.clients.elastic.dto.GetElasticNodeAndIndexCountsResponse;
import co.hyperflex.clients.elastic.dto.GetElasticsearchSnapshotResponse;
import co.hyperflex.clients.elastic.dto.cat.health.HealthRecord;
import co.hyperflex.clients.elastic.dto.cat.indices.FlushResponse;
import co.hyperflex.clients.elastic.dto.cat.indices.IndicesRecord;
import co.hyperflex.clients.elastic.dto.cat.master.MasterRecord;
import co.hyperflex.clients.elastic.dto.cat.shards.ShardsRecord;
import co.hyperflex.clients.elastic.dto.cluster.AllocationExplainRequest;
import co.hyperflex.clients.elastic.dto.cluster.AllocationExplainResponse;
import co.hyperflex.clients.elastic.dto.cluster.ClusterStatsResponse;
import co.hyperflex.clients.elastic.dto.cluster.GetClusterSettingsResponse;
import co.hyperflex.clients.elastic.dto.cluster.PutClusterSettingsResponse;
import co.hyperflex.clients.elastic.dto.cluster.health.ClusterHealthResponse;
import co.hyperflex.clients.elastic.dto.info.InfoResponse;
import co.hyperflex.clients.elastic.dto.nodes.NodesInfoResponse;
import co.hyperflex.clients.elastic.dto.nodes.NodesStatsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public class ElasticClientImpl implements ElasticClient {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticClientImpl.class);
  private final RestClient restClient;

  public ElasticClientImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public List<HealthRecord> getHealth() {
    String uri = "/_cat/health?format=json";
    return restClient.get().uri(uri).retrieve().body(new ParameterizedTypeReference<>() {
    });
  }

  @Override
  public String getHealthStatus() {
    return getHealth().get(0).getStatus();
  }

  @Override
  public List<IndicesRecord> getIndices() {
    var uri = "/_cat/indices?format=json";
    return restClient.get().uri(uri).retrieve().body(new ParameterizedTypeReference<>() {
    });
  }

  @Override
  public List<MasterRecord> getActiveMasters() {
    var uri = "/_cat/master?v&format=json";
    return restClient.get().uri(uri).retrieve().body(new ParameterizedTypeReference<>() {
    });
  }

  @Override
  public Boolean isAdaptiveReplicaEnabled() {
    GetClusterSettingsResponse settings = getClusterSettings();

    Map<String, Object> transientSettings = settings.getTransient();
    Map<String, Object> persistentSettings = settings.getPersistent();
    Map<String, Object> defaultSettings = settings.getDefaults();

    String value = null;

    if (transientSettings.containsKey("search.adaptive_replica_selection")) {
      value = transientSettings.get("search.adaptive_replica_selection").toString();
    } else if (persistentSettings.containsKey("search.adaptive_replica_selection")) {
      value = persistentSettings.get("search.adaptive_replica_selection").toString();
    } else if (defaultSettings.containsKey("search.adaptive_replica_selection")) {
      value = defaultSettings.get("search.adaptive_replica_selection").toString();
    }
    return Boolean.parseBoolean(value);
  }

  @Override
  public List<GetElasticsearchSnapshotResponse> getValidSnapshots() {
    try {
      ResponseEntity<JsonNode> repoResponse = restClient.get()
          .uri("/_snapshot")
          .retrieve()
          .toEntity(JsonNode.class);

      JsonNode repoResult = repoResponse.getBody();
      if (repoResult == null || repoResult.isEmpty()) {
        return Collections.emptyList();
      }

      Set<String> repositories = new HashSet<>();
      repoResult.fieldNames().forEachRemaining(repositories::add);

      if (repositories.isEmpty()) {
        return Collections.emptyList();
      }

      long now = System.currentTimeMillis();
      long twentyFourHoursAgo = now - 24 * 60 * 60 * 1000;

      List<GetElasticsearchSnapshotResponse> allValidSnapshots =
          repositories.stream().parallel().flatMap(repository -> {
            try {
              ResponseEntity<JsonNode> snapshotResponse = restClient.get()
                  .uri("/_snapshot/{repo}/_all", repository)
                  .retrieve()
                  .toEntity(JsonNode.class);

              JsonNode body = snapshotResponse.getBody();
              if (body == null || !body.has("snapshots")) {
                return Stream.empty();
              }

              JsonNode snapshots = body.get("snapshots");
              return StreamSupport.stream(snapshots.spliterator(), false)
                  .filter(s -> s.has("start_time_in_millis"))
                  .filter(s -> {
                    long time = s.get("start_time_in_millis").asLong();
                    return time >= twentyFourHoursAgo && time <= now;
                  })
                  .map(s -> new GetElasticsearchSnapshotResponse(
                      s.get("snapshot").asText(),
                      new Date(s.get("start_time_in_millis").asLong())
                  ));
            } catch (Exception e) {
              LOG.error("Error fetching snapshots for repository {}: {}", repository, e.getMessage(), e);
              return Stream.empty();
            }
          }).toList();

      if (allValidSnapshots.isEmpty()) {
        LOG.info("No valid snapshots found within the last 24 hours.");
      }

      return allValidSnapshots;

    } catch (Exception e) {
      LOG.error("Error checking snapshot details:", e);
      throw new RuntimeException("Failed to get valid snapshots", e);
    }
  }

  @Override
  public GetElasticNodeAndIndexCountsResponse getEntitiesCounts() {
    ClusterHealthResponse health = getClusterHealth();
    int totalIndices = getIndices().size();
    int activePrimaryShards = health.getActivePrimaryShards();
    int activeShards = health.getActiveShards();
    int unassignedShards = health.getUnassignedShards();
    int initializingShards = health.getInitializingShards();
    int relocatingShards = health.getRelocatingShards();
    var stats = getClusterStats();
    var nodeCount = stats.getNodes().getCount();
    int totalNodes = nodeCount.getTotal();
    int dataNodes = nodeCount.getData()
        + nodeCount.getDataCold()
        + nodeCount.getDataContent()
        + nodeCount.getDataHot()
        + nodeCount.getDataWarm();
    int masterNodes = nodeCount.getMaster();

    return new GetElasticNodeAndIndexCountsResponse(dataNodes, totalNodes, masterNodes,
        totalIndices, activePrimaryShards, activeShards, unassignedShards, initializingShards,
        relocatingShards);
  }

  @Override
  public GetElasticDeprecationResponse getDeprecation() {
    return restClient.get().uri("/_migration/deprecations").retrieve().body(GetElasticDeprecationResponse.class);
  }


  @Override
  public List<GetAllocationExplanationResponse> getAllocationExplanation() {
    String unassigned = "UNASSIGNED";
    List<ShardsRecord> shards = getShards();
    return shards.stream().filter(s -> unassigned.equals(s.getState())).map(shard -> {
      boolean isPrimaryShard = "p".equals(shard.getPrirep());
      var body = "{\"index\":\"" + shard.getIndex() + "\",\"shard\":" + shard.getShard() + ",\"primary\":" + isPrimaryShard + "}";
      Map data = restClient.post().uri("/_cluster/allocation/explain").body(body).retrieve().body(Map.class);
      return new GetAllocationExplanationResponse(
          shard.getIndex(),
          shard.getShard() + (isPrimaryShard ? "(primary)" : ""),
          data.get("allocate_explanation").toString()
      );
    }).toList();
  }

  @Override
  public AllocationExplainResponse getAllocationExplanation(AllocationExplainRequest request) {
    Map<String, Object> requestBody = Map.of(
        "index", request.index(),
        "shard", request.shard(),
        "primary", request.primary()
    );
    return restClient
        .post()
        .uri("/_cluster/allocation/explain")
        .body(requestBody)
        .retrieve()
        .body(AllocationExplainResponse.class);
  }

  @Override
  public co.hyperflex.clients.elastic.dto.cluster.GetClusterSettingsResponse getClusterSettings() {
    String uri = "/_cluster/settings?flat_settings=true&include_defaults=false&format=json";
    return restClient.get().uri(uri).retrieve().body(co.hyperflex.clients.elastic.dto.cluster.GetClusterSettingsResponse.class);
  }

  @Override
  public PutClusterSettingsResponse updateClusterSettings(Map<String, Object> clusterSettings) {
    String uri = "/_cluster/settings?format=json";
    return restClient.put().uri(uri).body(clusterSettings).retrieve().body(PutClusterSettingsResponse.class);
  }

  @Override
  public List<ShardsRecord> getShards() {
    return getShards("");
  }

  @Override
  public List<ShardsRecord> getShards(String indexName) {
    String uri = "/_cat/shards/" + indexName + "?format=json";
    return restClient.get().uri(uri).retrieve().body(new ParameterizedTypeReference<>() {
    });
  }

  @Override
  public FlushResponse flushIndices() {
    return restClient.post().uri("/_flush").retrieve().body(FlushResponse.class);
  }

  @Override
  public ClusterHealthResponse getClusterHealth() {
    return performGet("/_cluster/health", ClusterHealthResponse.class);
  }

  @Override
  public InfoResponse getInfo() {
    return performGet("/", InfoResponse.class);
  }

  @Override
  public NodesInfoResponse getNodeInfo(String nodeId) {
    String uri = "/_nodes/" + nodeId;
    return performGet(uri, NodesInfoResponse.class);
  }

  @Override
  public NodesInfoResponse getNodesInfo() {
    return getNodeInfo("");
  }

  @Override
  public ClusterStatsResponse getClusterStats() {
    var uri = "/_cluster/stats?format=json";
    return performGet(uri, ClusterStatsResponse.class);
  }

  @Override
  public NodesStatsResponse getNodesMetric(String nodeId) {
    return getNodesMetric(nodeId, "stats");
  }

  @Override
  public NodesStatsResponse getNodesMetric(String nodeId, String metric) {
    String uri = "/_nodes/" + nodeId + "/" + metric;
    return performGet(uri, NodesStatsResponse.class);
  }

  public <T> T performGet(String uri, Class<T> responseType) {
    return restClient.get().uri(uri).retrieve().body(responseType);
  }

  @Override
  public <T> T execute(ElasticRequest<T> request) {
    Consumer<HttpHeaders> httpHeadersConsumer = httpHeaders -> {
      Map<String, Object> headers = request.getHeaders();
      if (headers != null) {
        headers.forEach((name, value) -> httpHeaders.add(name, String.valueOf(value)));
      }
    };
    ResponseEntity<T> response = switch (request.getMethod()) {
      case GET -> restClient.get()
          .uri(request.getUri())
          .headers(httpHeadersConsumer)
          .retrieve()
          .toEntity(request.getResponseType());

      case POST -> restClient.post()
          .uri(request.getUri())
          .headers(httpHeadersConsumer)
          .body(request.getBody())
          .retrieve()
          .toEntity(request.getResponseType());

      case PUT -> restClient.put()
          .uri(request.getUri())
          .headers(httpHeadersConsumer)
          .body(request.getBody())
          .retrieve()
          .toEntity(request.getResponseType());

      case DELETE -> restClient.delete()
          .uri(request.getUri())
          .headers(httpHeadersConsumer)
          .retrieve()
          .toEntity(request.getResponseType());
    };

    return response.getBody();
  }
}
