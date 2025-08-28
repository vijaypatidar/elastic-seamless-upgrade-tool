package co.hyperflex.pluginmanager;

import java.util.Set;

public class PluginRegistry {
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


}