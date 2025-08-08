package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;

public class SetDefaultIndexTask implements Task {

  @Override
  public String getName() {
    return "Set default index(syslog)";
  }

  @Override
  public TaskResult run(Context context) {
    final Logger logger = context.logger();
    final String host = context.node().getIp();
    final KibanaClient kibanaClient = context.kibanaClient();

    logger.info("Setting default index");

    String url = "http://" + host + ":5601/api/kibana/settings";
    try {
      String requestBody = "{\"changes\":{\"defaultIndex\":\"syslog\"}}";

      Map<String, Object> response = kibanaClient.getRestClient()
          .post()
          .uri(url)
          .header("kbn-version", context.config().targetVersion())
          .body(requestBody)
          .retrieve()
          .body(new ParameterizedTypeReference<>() {
          });
      logger.info("Successfully set default index.");
      return TaskResult.success("Default index set.");
    } catch (Exception e) {
      logger.error("Failed to set default index.", e);
      return TaskResult.failure("Failed to set default index.");
    }

  }
}
