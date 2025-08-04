package co.hyperflex.prechecks.concrete.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class IndexFieldCountPrecheck extends BaseIndexPrecheck {

  private static final int fieldLimit = 1000; // Example threshold

  @Override
  public String getName() {
    return "Mapped Field Count Check";
  }

  @Override
  public void run(IndexContext context) {
    String indexName = context.getIndexName();
    ElasticsearchClient elasticsearchClient = context.getElasticClient().getElasticsearchClient();
    Logger logger = context.getLogger();
    try {
      GetMappingResponse response =
          elasticsearchClient.indices().getMapping(GetMappingRequest.of(r -> r.index(indexName)));
      Map<String, IndexMappingRecord> mappings = response.result();
      IndexMappingRecord indexMapping = mappings.get(indexName);

      if (Optional.ofNullable(indexMapping).map(IndexMappingRecord::mappings)
          .map(TypeMapping::properties).isEmpty()) {
        logger.info("Index [{}] has no properties defined.", indexName);
        return;
      }

      Map<String, Property> properties = indexMapping.mappings().properties();
      int fieldCount = countFields(properties);

      logger.info("Index [{}] has {} mapped fields.", indexName, fieldCount);

      if (fieldCount > fieldLimit) {
        logger.warn(
            "Index [{}] exceeds the recommended field count ({} > {}). Consider flattening mappings.",
            indexName, fieldCount, fieldLimit);
      }
    } catch (IOException e) {
      logger.error("Failed to get field count for index: {}", indexName, e);
      throw new RuntimeException(e);
    }

  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  private int countFields(Map<String, Property> properties) {
    // Recursively count nested fields
    int count = 0;
    for (Map.Entry<String, Property> entry : properties.entrySet()) {
      count++;
      Property prop = entry.getValue();
      if (prop._kind() == Property.Kind.Object && prop.object().properties() != null) {
        count += countFields(prop.object().properties());
      } else if (prop._kind() == Property.Kind.Nested && prop.nested().properties() != null) {
        count += countFields(prop.nested().properties());
      }
    }
    return count;
  }
}
