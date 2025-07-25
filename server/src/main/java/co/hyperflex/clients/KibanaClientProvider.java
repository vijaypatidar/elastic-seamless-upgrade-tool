package co.hyperflex.clients;

import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterRepository;
import org.apache.http.Header;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

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
    return clusterRepository.findById(clusterId).map(cluster -> {
      Header authHeader = credentialProvider.getAuthHeader(clusterId);
      RestTemplateBuilder builder = new RestTemplateBuilder()
          .interceptors((request, body, execution) -> {
            request.getHeaders().add(authHeader.getName(), authHeader.getValue());
            request.getHeaders().add("kbn-xsrf", "true");
            return execution.execute(request, body);
          });
      return new KibanaClient(builder.build(), cluster.getKibanaUrl());
    }).orElseThrow(() -> new NotFoundException("Cluster not found"));

  }
}
