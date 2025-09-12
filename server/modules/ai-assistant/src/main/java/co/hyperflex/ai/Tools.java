package co.hyperflex.ai;

import dev.langchain4j.agent.tool.Tool;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class Tools {

  @Tool("Get the current and target Elasticsearch versions for the upgrade job")
  public String getUpgradeJobInfo() {
    return "Current version: 8.17.6, Target version: 8.19.0";
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
