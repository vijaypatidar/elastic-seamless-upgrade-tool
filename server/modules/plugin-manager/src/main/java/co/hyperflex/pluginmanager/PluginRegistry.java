package co.hyperflex.pluginmanager;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PluginRegistry {
  private static final Map<String, String> pluginRepo = Map.of("analysis-ik", "https://get.infini.cloud/elasticsearch/analysis-ik/");
  private static final Set<String> OFFICIAL_ELASTICSEARCH_PLUGINS = Set.of(
      "analysis-icu", "analysis-kuromoji", "analysis-nori",
      "analysis-phonetic", "analysis-smartcn", "analysis-stempel",
      "analysis-ukrainian", "discovery-azure-classic", "discovery-ec2",
      "discovery-gce", "ingest-attachment", "ingest-geoip",
      "ingest-user-agent", "mapper-annotated-text", "mapper-murmur3",
      "mapper-size", "repository-azure", "repository-gcs",
      "repository-hdfs", "repository-s3", "store-smb", "transport-nio"
  );

  public boolean isOfficial(String pluginName) {
    return OFFICIAL_ELASTICSEARCH_PLUGINS.contains(pluginName);
  }

  @Nullable
  public String getPluginSource(String pluginName) {
    return pluginRepo.get(pluginName);
  }
}