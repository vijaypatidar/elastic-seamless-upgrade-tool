package co.hyperflex.clients.kibana;

import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.common.exceptions.NotFoundException;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.repositories.ClusterRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KibanaClientProvider {

  private final ClusterCredentialProvider credentialProvider;
  private final ClusterRepository clusterRepository;

  public KibanaClientProvider(ClusterCredentialProvider credentialProvider,
                              ClusterRepository clusterRepository) {
    this.credentialProvider = credentialProvider;
    this.clusterRepository = clusterRepository;
  }

  public KibanaClient getClient(String clusterId) {
    return clusterRepository.findById(clusterId).map(this::getClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  public KibanaClient getClient(@NotNull ClusterEntity cluster) {
    var authHeader = credentialProvider.getAuthHeader(cluster);

    RestClient client = RestClient.builder()
        .baseUrl(cluster.getKibanaUrl())
        .defaultHeader(authHeader.key(), authHeader.value())
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("kbn-xsrf", "true")
        .build();

    return new KibanaClientImpl(client, cluster.getKibanaUrl());
  }
}
