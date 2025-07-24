package co.hyperflex.clients;

import java.util.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

@Component
public class ClusterCredentialProvider {
  public Header getAuthHeader(String clusterId) {
    String apiKey = "TFNzWExKZ0JwZ0w0WjN3RVlLeUM6Q2pNRXdIVUxSMXVxNWs3ckhoUDN4QQ==";

    Header authHeader;
    Object username = "elastic";
    Object password = null;

    if (apiKey != null && !apiKey.isBlank()) {
      authHeader = new BasicHeader("Authorization", "ApiKey " + apiKey);
    } else if (username != null && password != null) {
      String encodedCreds = Base64.getEncoder()
          .encodeToString((username + ":" + password).getBytes());
      authHeader = new BasicHeader("Authorization", "Basic " + encodedCreds);
    } else {
      throw new IllegalArgumentException("Either apiKey or username/password must be provided");
    }
    return authHeader;
  }
}
