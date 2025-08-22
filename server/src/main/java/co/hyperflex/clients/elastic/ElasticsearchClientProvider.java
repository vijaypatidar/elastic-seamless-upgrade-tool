package co.hyperflex.clients.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


@Component
public class ElasticsearchClientProvider {

  private final Logger logger = LoggerFactory.getLogger(ElasticsearchClientProvider.class);
  private final ClusterCredentialProvider credentialProvider;
  private final ClusterRepository clusterRepository;
  private final ObjectMapper objectMapper;

  public ElasticsearchClientProvider(ClusterCredentialProvider credentialProvider,
                                     ClusterRepository clusterRepository,
                                     ObjectMapper objectMapper) {
    this.credentialProvider = credentialProvider;
    this.clusterRepository = clusterRepository;
    this.objectMapper = objectMapper;
  }

  @Cacheable(value = "elasticClientCache", key = "#clusterId")
  public ElasticClient getClientByClusterId(@NotNull String clusterId) {
    return clusterRepository.findById(clusterId).map(ElasticsearchClientProvider.this::buildElasticClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  public ElasticClient buildElasticClient(ClusterEntity cluster) {
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
      RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
      return new ElasticClient(new ElasticsearchClient(transport), restClient, objectMapper);
    } catch (Exception e) {
      logger.error("Failed to create elasticsearch client for cluster {}", cluster.getId(), e);
      throw new RuntimeException(e);
    }
  }
}
