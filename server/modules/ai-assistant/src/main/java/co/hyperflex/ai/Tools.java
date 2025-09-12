package co.hyperflex.ai;

import co.hyperflex.clients.elastic.ElasticsearchClientProvider;
import co.hyperflex.common.client.ApiRequest;
import co.hyperflex.core.services.clusters.ClusterService;
import co.hyperflex.core.services.clusters.dtos.GetClusterNodeResponse;
import co.hyperflex.core.services.upgrade.ClusterUpgradeJobService;
import co.hyperflex.core.upgrade.ClusterUpgradeJobEntity;
import dev.langchain4j.agent.tool.Tool;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Tools {
  private final ClusterUpgradeJobService clusterUpgradeJobService;
  private final ClusterService clusterService;
  private final ElasticsearchClientProvider elasticsearchClientProvider;

  public Tools(ClusterUpgradeJobService clusterUpgradeJobService, ClusterService clusterService,
               ElasticsearchClientProvider elasticsearchClientProvider) {
    this.clusterUpgradeJobService = clusterUpgradeJobService;
    this.clusterService = clusterService;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
  }

  @Tool("Get the current and target Elasticsearch versions for the upgrade job")
  public ClusterUpgradeJobEntity getUpgradeJobInfo() {
    var clusterId = SessionContextHolder.getSessionContext().clusterId();
    return clusterUpgradeJobService.getLatestJobByClusterId(clusterId);
  }

  @Tool("Get all elastic and kibana nodes of elastic cluster")
  public List<GetClusterNodeResponse> getNodes() {
    var clusterId = SessionContextHolder.getSessionContext().clusterId();
    return clusterService.getNodes(clusterId);
  }

  @Tool("Perform any GET request on any node of the Elasticsearch cluster using a relative path")
  public String performGetOnElastic(String relativePath) {
    var clusterId = SessionContextHolder.getSessionContext().clusterId();
    var client = elasticsearchClientProvider.getClient(clusterId);
    return client.execute(ApiRequest.builder(String.class).get().uri(relativePath).build());
  }

  @Tool("Perform an HTTP GET request for the provided URL and return the response body as text")
  public String httpGet(String url) {
    try (var client = HttpClient.newHttpClient()) {
      var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (Exception e) {
      return "Error fetching URL: " + e.getMessage();
    }
  }
}
