package co.hyperflex.prechecks.concrete.node.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.Process;
import co.elastic.clients.elasticsearch.nodes.Stats;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseElasticNodePrecheck;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FileDescriptorLimitPrecheck extends BaseElasticNodePrecheck {

  private static final long MIN_LIMIT = 65535;

  @Override
  public String getName() {
    return "File Descriptor Limit Check";
  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  @Override
  public void run(NodeContext context) {
    String nodeId = context.getNode().getId();
    ElasticsearchClient client = context.getElasticClient().getElasticsearchClient();
    Logger logger = context.getLogger();

    try {
      NodesStatsResponse response = client.nodes().stats(r -> r.nodeId(nodeId).metric("process"));

      Map<String, Stats> nodes = response.nodes();
      Stats node = nodes.get(nodeId);

      if (node == null) {
        throw new RuntimeException("Node with ID [" + nodeId + "] not found in cluster");
      }

      String name = node.name();
      Process process = node.process();

      if (process == null || process.maxFileDescriptors() == null
          || process.openFileDescriptors() == null) {
        logger.info("{}: Skipping file descriptor check — missing metrics.", name);
        return;
      }

      long maxFD = process.maxFileDescriptors();
      long openFD = process.openFileDescriptors();
      double usagePercent = (double) openFD / maxFD * 100;

      logger.info("{}: Open FDs = {}, Max FDs = {} ({}% in use)", name, openFD, maxFD, usagePercent);

      if (maxFD < MIN_LIMIT) {
        logger.error("{}: Max file descriptor limit ({}) is below the recommended minimum ({}).", name, maxFD, MIN_LIMIT);
        logger.error("Consider increasing 'ulimit -n' and systemd LimitNOFILE.");
        throw new RuntimeException();
      }

    } catch (IOException e) {
      logger.error("Failed to retrieve file descriptor stats for node: {}", nodeId, e);
      throw new RuntimeException("Failed to retrieve file descriptor stats for node: " + nodeId, e);
    }
  }
}
