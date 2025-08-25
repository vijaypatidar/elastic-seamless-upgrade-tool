package co.hyperflex.clients;

import co.hyperflex.entities.cluster.ClusterEntity;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClusterCredentialProvider {

  private static final Logger logger = LoggerFactory.getLogger(ClusterCredentialProvider.class);

  public Header getAuthHeader(ClusterEntity cluster) {
    if (!Optional.ofNullable(cluster.getApiKey()).orElse("").isEmpty()) {
      return new Header("Authorization", "ApiKey " + cluster.getApiKey());
    } else if (cluster.getUsername() != null && cluster.getPassword() != null) {
      String encodedCred = Base64.getEncoder()
          .encodeToString((cluster.getUsername() + ":" + cluster.getPassword()).getBytes());
      return new Header("Authorization", "Basic " + encodedCred);
    } else {
      logger.error("Either apiKey or username/password must be provided for cluster {}",
          cluster.getId());
      throw new IllegalArgumentException("Either apiKey or username/password must be provided");
    }
  }
}
