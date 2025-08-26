package co.hyperflex.clients.kibana;

import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.common.client.ClientConnectionDetail;
import co.hyperflex.common.exceptions.NotFoundException;
import co.hyperflex.core.entites.clusters.ClusterEntity;
import co.hyperflex.core.repositories.ClusterRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KibanaClientProviderImpl implements KibanaClientProvider {

  private final ClusterCredentialProvider credentialProvider;
  private final ClusterRepository clusterRepository;

  public KibanaClientProviderImpl(ClusterCredentialProvider credentialProvider,
                                  ClusterRepository clusterRepository) {
    this.credentialProvider = credentialProvider;
    this.clusterRepository = clusterRepository;
  }

  @Override
  public KibanaClient getClient(String clusterId) {
    return clusterRepository.findById(clusterId).map(this::buildClientConnectionDetail)
        .map(this::getClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  @Override
  public KibanaClient getClient(ClientConnectionDetail detail) {
    var authHeader = detail.authHeader();
    RestClient client = RestClient.builder()
        .baseUrl(detail.baseUrl())
        .defaultHeader(authHeader.key(), authHeader.value())
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("kbn-xsrf", "true")
        .build();
    return new KibanaClientImpl(client, detail.baseUrl());
  }

  private ClientConnectionDetail buildClientConnectionDetail(ClusterEntity clusterEntity) {
    return new ClientConnectionDetail(
        clusterEntity.getKibanaUrl(),
        credentialProvider.getAuthHeader(clusterEntity)
    );
  }
}
