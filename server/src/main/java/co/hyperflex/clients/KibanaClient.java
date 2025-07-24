package co.hyperflex.clients;

import org.springframework.web.client.RestTemplate;

public class KibanaClient {

  private final RestTemplate restTemplate;

  public KibanaClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
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

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
