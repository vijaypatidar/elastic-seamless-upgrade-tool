package co.hyperflex.clients;

import co.hyperflex.entities.cluster.OperatingSystemInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class KibanaClient {

  private static final Logger logger = LoggerFactory.getLogger(KibanaClient.class);
  private final RestTemplate restTemplate;
  private final String kibanaUrl;

  public KibanaClient(RestTemplate restTemplate, String kibanaUrl) {
    this.restTemplate = restTemplate;
    this.kibanaUrl = kibanaUrl;
  }

  public boolean isKibanaReady(String host) {
    String url = "http://" + host + ":5601/api/kibana/settings";
    try {
      restTemplate.getForObject(url, String.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<Map<String, Object>> getDeprecations() {
    String url = kibanaUrl + "/api/upgrade_assistant/deprecations";
    try {
      return restTemplate.getForObject(url, List.class);
    } catch (Exception e) {
      return List.of();
    }
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public String getKibanaVersion() {
    return null;
  }

  public String getKibanaVersion(String nodeIp) {
    return getKibanaNodeDetails(nodeIp).version;
  }

  public KibanaNodeDetails getKibanaNodeDetails() {
    return this.getKibanaNodeDetails(null);
  }

  public KibanaNodeDetails getKibanaNodeDetails(String nodeIp) {

    String url = Optional.ofNullable(nodeIp).map(ip ->
        String.format("http://%s:5601/api/status", nodeIp)).orElse(this.kibanaUrl);

    try {
      Map response = restTemplate.getForObject(url, Map.class);

      // Extract fields
      String version = ((Map<String, String>) response.get("version")).get("number");

      Map<String, Object> osMap = (Map<String, Object>) response.get("os");

      OperatingSystemInfo os = null;
      if (osMap != null && osMap.containsKey("platform")) {
        os = new OperatingSystemInfo(osMap.get("platform").toString(), osMap.get("name").toString(),
            osMap.get("version").toString());
      }

      return new KibanaNodeDetails(version, os);
    } catch (RestClientException e) {
      logger.error("Error getting Kibana node details: {}", e.getMessage());
      throw e;
    }
  }

  public String getKibanaUrl() {
    return kibanaUrl;
  }

  public String getSnapshotCreationPageUrl() {
    return kibanaUrl + "/app/management/data/snapshot_restore/snapshots";
  }

  public record KibanaNodeDetails(
      String version,
      OperatingSystemInfo os
  ) {
  }
}
