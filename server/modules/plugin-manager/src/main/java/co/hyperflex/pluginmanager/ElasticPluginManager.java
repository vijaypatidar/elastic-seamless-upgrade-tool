package co.hyperflex.pluginmanager;

import co.hyperflex.ssh.CommandResult;
import co.hyperflex.ssh.SshCommandExecutor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ElasticPluginManager {
  private final SshCommandExecutor executor;
  private final String pluginCommand = "sudo /usr/share/elasticsearch/bin/elasticsearch-plugin ";
  private final Map<String, String> pluginRepo = Map.of("analysis-ik", "https://get.infini.cloud/elasticsearch/analysis-ik/");
  private static final Set<String> OFFICIAL_ELASTICSEARCH_PLUGINS = Set.of(
      "analysis-icu",
      "analysis-kuromoji",
      "analysis-nori",
      "analysis-phonetic",
      "analysis-smartcn",
      "analysis-stempel",
      "analysis-ukrainian",
      "discovery-azure-classic",
      "discovery-ec2",
      "discovery-gce",
      "ingest-attachment",
      "ingest-geoip",
      "ingest-user-agent",
      "mapper-annotated-text",
      "mapper-murmur3",
      "mapper-size",
      "repository-azure",
      "repository-gcs",
      "repository-hdfs",
      "repository-s3",
      "store-smb",
      "transport-nio" // older versions, removed in later 7.x
  );

  public ElasticPluginManager(SshCommandExecutor executor) {
    this.executor = executor;
  }

  public List<String> listPlugins() {
    try {
      CommandResult result = executor.execute(pluginCommand + "list");
      if (!result.isSuccess()) {
        throw new RuntimeException("Failed to list plugins: " + result.stderr());
      }
      return Arrays.stream(result.stdout().split("\n"))
          .map(String::trim)
          .filter(p -> !p.isBlank())
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void removePlugin(String pluginName) {
    try {
      CommandResult result = executor.execute(pluginCommand + "remove " + pluginName);
      if (!result.isSuccess()) {
        throw new RuntimeException("Failed to remove plugin " + pluginName + ": " + result.stderr());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isPluginAvailable(String pluginName, String version) {
    if (OFFICIAL_ELASTICSEARCH_PLUGINS.contains(pluginName)) {
      return true;
    } else {
      return PluginVerifier.verifyPlugin(getPluginSource(pluginName, version), version);
    }
  }

  public void installPlugin(String pluginName, String version) {
    try {
      String source = getPluginSource(pluginName, version);
      CommandResult result = executor.execute(pluginCommand + "install --batch " + source);
      if (!result.isSuccess()) {
        throw new RuntimeException("Failed to install [plugin: " + pluginName + "] from [source: " + source + "] : " + result.stderr());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getPluginSource(String pluginName, String version) {
    String source;
    if (OFFICIAL_ELASTICSEARCH_PLUGINS.contains(pluginName)) {
      source = pluginName;
    } else {
      source = Optional.ofNullable(pluginRepo.get(pluginName)).map(s -> s + version).orElseThrow();
    }
    return source;
  }

}
