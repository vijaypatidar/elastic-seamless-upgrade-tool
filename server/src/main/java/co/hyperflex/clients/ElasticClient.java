package co.hyperflex.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

public class ElasticClient {
  private final ElasticsearchClient elasticsearchClient;

  public ElasticClient(ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  public ElasticsearchClient getElasticsearchClient() {
    return elasticsearchClient;
  }
}
