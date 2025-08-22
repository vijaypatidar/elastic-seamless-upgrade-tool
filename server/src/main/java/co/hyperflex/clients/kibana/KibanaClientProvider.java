package co.hyperflex.clients.kibana;

import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterRepository;
import jakarta.validation.constraints.NotNull;
import org.apache.http.Header;
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

  public KibanaClient getKibanaClientByClusterId(String clusterId) {
    return clusterRepository.findById(clusterId).map(this::getClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  public KibanaClient getClient(@NotNull ClusterEntity cluster) {
    Header authHeader = credentialProvider.getAuthHeader(cluster);

    RestClient client = RestClient.builder()
        .baseUrl(cluster.getKibanaUrl())
        .defaultHeader(authHeader.getName(), authHeader.getValue())
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("kbn-xsrf", "true")
        .build();

    return new KibanaClient(client, cluster.getKibanaUrl());
  }
}
