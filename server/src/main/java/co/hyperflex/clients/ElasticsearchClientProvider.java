package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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

  public ElasticsearchClientProvider(ClusterCredentialProvider credentialProvider) {
    this.credentialProvider = credentialProvider;
  }

  public ElasticClient getElasticsearchClientByClusterId(String clusterId) {
    String serverUrl = "https://34.207.69.39:9200";

    try {
      SSLContext sslContext = SSLContextBuilder.create()
          .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
          .build();

      RestClient restClient = RestClient
          .builder(HttpHost.create(serverUrl))
          .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE))
          .setDefaultHeaders(new Header[] {
              credentialProvider.getAuthHeader(clusterId)
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
