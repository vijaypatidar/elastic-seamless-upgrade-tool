package co.hyperflex.clients.elastic;

import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterRepository;
import jakarta.validation.constraints.NotNull;
import java.net.Socket;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class ElasticsearchClientProviderImpl implements ElasticsearchClientProvider {

  private final Logger logger = LoggerFactory.getLogger(ElasticsearchClientProviderImpl.class);
  private final ClusterCredentialProvider credentialProvider;
  private final ClusterRepository clusterRepository;

  public ElasticsearchClientProviderImpl(ClusterCredentialProvider credentialProvider,
                                         ClusterRepository clusterRepository) {
    this.credentialProvider = credentialProvider;
    this.clusterRepository = clusterRepository;
  }

  @Cacheable(value = "elasticClientCache", key = "#clusterId")
  @Override
  public ElasticClient getClientByClusterId(@NotNull String clusterId) {
    return clusterRepository.findById(clusterId).map(ElasticsearchClientProviderImpl.this::buildElasticClient)
        .orElseThrow(() -> new NotFoundException("Cluster not found"));
  }

  @Override
  public ElasticClient buildElasticClient(ClusterEntity cluster) {
    try {
      var authHeader = credentialProvider.getAuthHeader(cluster);
      HttpClient jdkHttpClient = HttpClient.newBuilder()
          .sslContext(getSSLContext())
          .build();

      RestClient genericClient = RestClient.builder()
          .baseUrl(cluster.getElasticUrl())
          .defaultHeader(authHeader.key(), authHeader.value())
          .defaultHeader("Content-Type", "application/json")
          .requestFactory(new JdkClientHttpRequestFactory(jdkHttpClient))
          .build();

      return new ElasticClientImpl(genericClient);
    } catch (Exception e) {
      logger.error("Failed to create elasticsearch client for cluster {}", cluster.getId(), e);
      throw new RuntimeException(e);
    }
  }

  private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
    var trustManager = new X509ExtendedTrustManager() {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) {
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) {
      }


      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
      }


      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
      }
    };
    var sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] {trustManager}, new SecureRandom());
    return sslContext;
  }
}
