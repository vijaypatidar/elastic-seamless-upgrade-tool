package co.hyperflex.upgrader.tasks;

import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.entities.cluster.ClusterNode;
import org.slf4j.Logger;

public record Context(ClusterNode node, Configuration config, Logger logger,
                      ElasticClient elasticClient, KibanaClient kibanaClient) {
}
