package co.hyperflex.clients;

import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

public class KibanaClient {

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
}
