package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterRepository;
import jakarta.validation.constraints.NotNull;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;


@Component
public class ElasticsearchClientProvider {

  private final ClusterCredentialProvider credentialProvider;
  private final ClusterRepository clusterRepository;

  public ElasticsearchClientProvider(ClusterCredentialProvider credentialProvider,
                                     ClusterRepository clusterRepository) {
    this.credentialProvider = credentialProvider;
    this.clusterRepository = clusterRepository;
  }

  public ElasticClient getElasticsearchClientByClusterId(@NotNull String clusterId) {
    return clusterRepository.findById(clusterId).map(this::getClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  public ElasticClient getClient(Cluster cluster) {
    try {
      SSLContext sslContext = SSLContextBuilder.create()
          .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
          .build();
      RestClient restClient = RestClient
          .builder(HttpHost.create(cluster.getElasticUrl()))
          .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE))
          .setDefaultHeaders(new Header[] {
              credentialProvider.getAuthHeader(cluster)
          })
          .build();
      ElasticsearchTransport transport =
          new RestClientTransport(restClient, new JacksonJsonpMapper());
      return new ElasticClient(new ElasticsearchClient(transport));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
