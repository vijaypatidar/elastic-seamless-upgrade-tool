package co.hyperflex.pluginmanager;

import co.hyperflex.core.models.enums.ClusterNodeType;
import co.hyperflex.ssh.SshCommandExecutor;
import java.util.Map;

public interface PluginManagerFactory {
  static PluginManager create(SshCommandExecutor executor, ClusterNodeType nodeType) {
    final Map<String, String> pluginRepo = Map.of("analysis-ik", "https://get.infini.cloud/elasticsearch/analysis-ik/");
    if (nodeType == ClusterNodeType.ELASTIC) {
      return new ElasticPluginManager(
          executor,
          new DefaultPluginSourceResolver(new PluginRegistry(), pluginRepo),
          new ElasticPluginArtifactValidator()
      );
    } else if (nodeType == ClusterNodeType.KIBANA) {
      return new KibanaPluginManager(
          executor,
          new DefaultPluginSourceResolver(new PluginRegistry(), pluginRepo),
          new KibanaPluginArtifactValidator()
      );
    }
    throw new IllegalArgumentException("Unknown node type: " + nodeType);
  }
}
