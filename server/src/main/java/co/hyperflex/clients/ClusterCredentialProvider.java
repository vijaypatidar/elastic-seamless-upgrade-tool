package co.hyperflex.clients;

import co.hyperflex.entities.cluster.Cluster;
import java.util.Base64;
import java.util.Optional;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

@Component
public class ClusterCredentialProvider {

  public Header getAuthHeader(Cluster cluster) {
    if (!Optional.ofNullable(cluster.getApiKey()).orElse("").isEmpty()) {
      return new BasicHeader("Authorization", "ApiKey " + cluster.getApiKey());
    } else if (cluster.getUsername() != null && cluster.getPassword() != null) {
      String encodedCred = Base64.getEncoder()
          .encodeToString((cluster.getUsername() + ":" + cluster.getPassword()).getBytes());
      return new BasicHeader("Authorization", "Basic " + encodedCred);
    } else {
      throw new IllegalArgumentException("Either apiKey or username/password must be provided");
    }
  }
}
