package co.hyperflex.services;


import co.elastic.clients.elasticsearch.migration.DeprecationsResponse;
import co.elastic.clients.elasticsearch.migration.deprecations.Deprecation;
import co.hyperflex.clients.ElasticClient;
import co.hyperflex.clients.ElasticsearchClientProvider;
import co.hyperflex.clients.KibanaClient;
import co.hyperflex.clients.KibanaClientProvider;
import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponseModels;
import co.hyperflex.entities.upgrade.ClusterUpgradeJob;
import co.hyperflex.entities.upgrade.ClusterUpgradeStatus;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.repositories.ClusterUpgradeJobRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class PrecheckReportService {

  private final ClusterUpgradeJobRepository clusterUpgradeJobRepository;
  private final PrecheckRunService precheckRunService;
  private final KibanaClientProvider kibanaClientProvider;
  private final ElasticsearchClientProvider elasticsearchClientProvider;

  public PrecheckReportService(ClusterUpgradeJobRepository clusterUpgradeJobRepository,
                               PrecheckRunService precheckRunService,
                               KibanaClientProvider kibanaClientProvider,
                               ElasticsearchClientProvider elasticsearchClientProvider) {
    this.clusterUpgradeJobRepository = clusterUpgradeJobRepository;
    this.precheckRunService = precheckRunService;
    this.kibanaClientProvider = kibanaClientProvider;
    this.elasticsearchClientProvider = elasticsearchClientProvider;
  }

  public String generatePrecheckReportMdContent(String clusterId) {
    ClusterUpgradeJob job = clusterUpgradeJobRepository.findByClusterIdAndStatusIsNot(clusterId,
            ClusterUpgradeStatus.UPDATED)
        .stream().findAny()
        .orElseThrow(() -> new NotFoundException("Cluster upgrade job not found"));
    final String currentVersion = job.getCurrentVersion();
    final String targetVersion = job.getTargetVersion();

    final GetGroupedPrecheckResponseModels.GetGroupedPrecheckResponse groupedPrechecks =
        precheckRunService.getGroupedPrecheckByClusterId(clusterId);

    StringBuilder md = new StringBuilder();
    md.append("# 📋 Elasticsearch Pre-check Report\n\n");
    md.append("Generated on: ").append(Instant.now()).append("\n\n");

    // Node summary
    md.append("## ✅ Node Summary\n");
    md.append("| Node Name | IP | Status |\n");
    md.append("|-----------|----|--------|\n");
    groupedPrechecks.node().forEach(node -> {
      md.append(String.format("| %s | %s | %s |\n", node.name(), node.ip(), node.status()));
    });

    md.append("\n## 🔍 Detailed Pre-checks\n");
    groupedPrechecks.node().forEach(node -> {
      md.append(String.format("\n### 🖥️ %s (%s)\n", node.name(), node.ip()));
      md.append("| Check | Status | Duration (s) |\n");
      md.append("|-------|--------|---------------|\n");
      for (GetGroupedPrecheckResponseModels.GetPrecheckEntry check : node.prechecks()) {
        md.append(
            String.format("| %s | %s | %s |\n", check.name(), check.status(), check.duration()));
      }

      md.append("\n<details><summary>Show Logs</summary>\n\n");
      for (GetGroupedPrecheckResponseModels.GetPrecheckEntry check : node.prechecks()) {
        md.append("#### ").append(check.name()).append("\n");
        List<String> logs = check.logs() != null && !check.logs().isEmpty()
            ? check.logs()
            : List.of("N/A");
        md.append("```\n").append(String.join("\n", logs)).append("\n```\n");
      }
      md.append("</details>\n");
    });

    // Index summary
    md.append("\n\n## ✅ Index Summary\n");
    md.append("| Index Name | Status |\n");
    md.append("|-----------|--------|\n");
    for (var index : groupedPrechecks.index()) {
      md.append(String.format("| %s | %s |\n", index.name(), index.status()));
    }

    md.append("\n## 🔍 Detailed Pre-checks\n");
    for (var index : groupedPrechecks.index()) {
      md.append(String.format("\n### 🖥️ %s\n", index.name()));
      md.append("| Check | Status | Duration (s) |\n");
      md.append("|-------|--------|---------------|\n");
      for (var check : index.prechecks()) {
        md.append(
            String.format("| %s | %s | %s |\n", check.name(), check.status(), check.duration()));
      }

      md.append("\n<details><summary>Show Logs</summary>\n\n");
      for (var check : index.prechecks()) {
        md.append("#### ").append(check.name()).append("\n");
        List<String> logs = check.logs() != null && !check.logs().isEmpty()
            ? check.logs()
            : List.of("N/A");
        md.append("```\n").append(String.join("\n", logs)).append("\n```\n");
      }
      md.append("</details>\n");
    }

    md.append("\n\n").append(getESDeprecationsMdReport(clusterId));
    md.append("\n\n").append(getKibanaDeprecationsMdReport(clusterId));
    md.append("\n\n").append(getBreakingChangesMdReport(currentVersion, targetVersion));


    return md.toString();
  }

  private String getBreakingChangesMdReport(String currentVersion, String targetVersion) {
    // TODO
    return "";
  }

  private String getESDeprecationsMdReport(String clusterId) {
    ElasticClient client = elasticsearchClientProvider.getElasticsearchClientByClusterId(clusterId);
    try {
      DeprecationsResponse deprecations =
          client.getElasticsearchClient().migration().deprecations();

      StringBuilder md = new StringBuilder("## ⚠️ Elasticsearch Deprecations\n\n");
      boolean found = false;

      if (!deprecations.clusterSettings().isEmpty()) {
        found = true;
        md.append("### Cluster Settings\n");
        deprecations.clusterSettings().forEach(getDeprecationConsumer(md));
        md.append("\n");
      }

      if (!deprecations.nodeSettings().isEmpty()) {
        found = true;
        md.append("### Node Settings\n");
        deprecations.nodeSettings().forEach(getDeprecationConsumer(md));
        md.append("\n");
      }

      if (!deprecations.indexSettings().isEmpty()) {
        found = true;
        md.append("### Index Settings\n");
        deprecations.indexSettings().forEach((key, value) -> {
          md.append("#### ").append(key).append("\n");
          value.forEach(getDeprecationConsumer(md));
        });
        md.append("\n");
      }

      if (!deprecations.mlSettings().isEmpty()) {
        found = true;
        md.append("### ML Settings\n");
        deprecations.mlSettings().forEach(getDeprecationConsumer(md));
        md.append("\n");
      }


      if (!found) {
        md.append("N/A");
      }
      return md.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Consumer<Deprecation> getDeprecationConsumer(StringBuilder md) {
    return issue -> {
      md.append("- **").append(issue.message()).append("**\n");
      if (issue.details() != null) {
        md.append("  - Details: ").append(issue.details()).append("\n");
      }
      if (issue.level() != null) {
        md.append("  - Level: `").append(issue.level()).append("`\n");
      }
    };
  }

  private String getKibanaDeprecationsMdReport(String clusterId) {
    KibanaClient client = kibanaClientProvider.getKibanaClientByClusterId(clusterId);
    List<Map<String, Object>> items = client.getDeprecations();

    StringBuilder md = new StringBuilder("## ⚠️ Kibana Deprecations\n\n");

    if (items.isEmpty()) {
      md.append("N/A");
    } else {
      for (Map<String, Object> item : items) {
        md.append("- **").append(item.get("title")).append("**\n");
        if (item.containsKey("message")) {
          md.append("  - ").append(item.get("message")).append("\n");
        }
        if (item.containsKey("level")) {
          md.append("  - Level: `").append(item.get("level")).append("`\n");
        }

        Map<String, Object> correctiveActions = (Map<String, Object>) item.get("correctiveActions");
        if (correctiveActions != null && correctiveActions.containsKey("manualSteps")) {
          List<String> steps = (List<String>) correctiveActions.get("manualSteps");
          if (!steps.isEmpty()) {
            md.append("  - Manual Steps:\n");
            for (String step : steps) {
              md.append("    - ").append(step).append("\n");
            }
          }
        }
        md.append("\n");
      }
    }

    return md.toString();
  }
}

