package co.hyperflex.clients;

import org.apache.http.Header;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

@Component
public class KibanaClientProvider {

  private final ClusterCredentialProvider credentialProvider;

  public KibanaClientProvider(ClusterCredentialProvider credentialProvider) {
    this.credentialProvider = credentialProvider;
  }

  public KibanaClient getKibanaClientByClusterId(String clusterId) {
    Header authHeader = credentialProvider.getAuthHeader(clusterId);
    RestTemplateBuilder builder = new RestTemplateBuilder()
        .interceptors((request, body, execution) -> {
          request.getHeaders().add(authHeader.getName(), authHeader.getValue());
          request.getHeaders().add("kbn-xsrf", "true");
          return execution.execute(request, body);
        });
    return new KibanaClient(builder.build());
  }
}
