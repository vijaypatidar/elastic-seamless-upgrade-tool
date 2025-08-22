package co.hyperflex.prechecks.concrete.index;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class IndexFieldCountPrecheck extends BaseIndexPrecheck {

  private static final int FIELD_LIMIT = 1000; // Example threshold


  @Override
  public String getName() {
    return "Mapped Field Count Check";
  }

  @Override
  public void run(IndexContext context) {
    String indexName = context.getIndexName();
    Logger logger = context.getLogger();

    JsonNode root = context.getElasticClient().getRestClient().get().uri("/" + indexName + "/_mapping")
        .retrieve()
        .body(JsonNode.class);
    JsonNode propertiesNode = root.path(indexName).path("mappings").path("properties");
    if (propertiesNode.isMissingNode() || propertiesNode.isEmpty()) {
      logger.info("Index [{}] has no properties defined.", indexName);
      return;
    }

    int fieldCount = countFields(propertiesNode);
    logger.info("Index [{}] has {} mapped fields.", indexName, fieldCount);

    if (fieldCount > FIELD_LIMIT) {
      logger.warn(
          "Index [{}] exceeds the recommended field count ({} > {}). Consider flattening mappings.",
          indexName, fieldCount, FIELD_LIMIT
      );
    }

  }

  @Override
  public PrecheckSeverity getSeverity() {
    return PrecheckSeverity.WARNING;
  }

  private int countFields(JsonNode propertiesNode) {
    int count = 0;
    Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();

    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      count++;

      JsonNode propNode = entry.getValue();
      JsonNode nestedProps = null;

      if (propNode.has("type") && propNode.get("type").asText().equals("object")) {
        nestedProps = propNode.path("properties");
      } else if (propNode.has("type") && propNode.get("type").asText().equals("nested")) {
        nestedProps = propNode.path("properties");
      }

      if (nestedProps != null && !nestedProps.isMissingNode() && !nestedProps.isEmpty()) {
        count += countFields(nestedProps);
      }
    }

    return count;
  }
}
