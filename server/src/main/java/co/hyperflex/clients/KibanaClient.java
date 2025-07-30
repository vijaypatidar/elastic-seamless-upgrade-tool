package co.hyperflex.clients;

import co.hyperflex.entities.cluster.OperatingSystemInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class KibanaClient {

  private static final Logger logger = LoggerFactory.getLogger(KibanaClient.class);
  private final RestClient restClient;
  private final String kibanaUrl;

  public KibanaClient(RestClient restClient, String kibanaUrl) {
    this.restClient = restClient;
    this.kibanaUrl = kibanaUrl;
  }

  public boolean isKibanaReady(String host) {
    String url = "http://" + host + ":5601/api/kibana/settings";
    try {
      restClient.get().uri(url).retrieve().toBodilessEntity();
      return true;
    } catch (Exception e) {
      logger.error("Failed to check if kibana is ready on host {}", host, e);
      return false;
    }
  }

  public List<Map<String, Object>> getDeprecations() {
    String url = kibanaUrl + "/api/upgrade_assistant/deprecations";
    try {
      return restClient.get().uri(url).retrieve().body(new ParameterizedTypeReference<>() {
      });
    } catch (Exception e) {
      logger.error("Failed to get deprecations from kibana", e);
      return List.of();
    }
  }

  public String getKibanaVersion(String nodeIp) {
    return getKibanaNodeDetails(nodeIp).version;
  }

  public KibanaNodeDetails getKibanaNodeDetails(String nodeIp) {
    String url =
        Optional.ofNullable(nodeIp).map(ip -> String.format("http://%s:5601/api/status", ip))
            .orElse(kibanaUrl + "/api/status");

    try {
      Map<String, Object> response =
          restClient.get().uri(url).retrieve().body(new ParameterizedTypeReference<>() {
          });

      String version = ((Map<String, String>) response.get("version")).get("number");

      OperatingSystemInfo os = null;
      if (response.containsKey("metrics") && response.get("metrics") instanceof Map metrics) {
        if (metrics.containsKey("os") && metrics.get("os") instanceof Map osMap) {
          os = new OperatingSystemInfo(osMap.get("platform").toString(),
              osMap.get("platformRelease").toString());
        }
      }

      return new KibanaNodeDetails(version, os);
    } catch (RestClientException e) {
      logger.error("Error getting Kibana node details: {}", e.getMessage());
      throw e;
    }
  }

  public String getSnapshotCreationPageUrl() {
    return kibanaUrl + "/app/management/data/snapshot_restore/snapshots";
  }

  public RestClient getRestClient() {
    return restClient;
  }

  public record KibanaNodeDetails(String version, OperatingSystemInfo os) {
  }
}
