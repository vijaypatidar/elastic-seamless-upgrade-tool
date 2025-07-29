package co.hyperflex.upgrader.tasks.kibana;

import co.hyperflex.clients.KibanaClient;
import co.hyperflex.upgrader.tasks.Context;
import co.hyperflex.upgrader.tasks.Task;
import co.hyperflex.upgrader.tasks.TaskResult;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class SetDefaultIndexTask implements Task {

  @Override
  public TaskResult run(Context context) {
    final Logger logger = context.logger();
    final String host = context.node().getIp();
    final KibanaClient kibanaClient = context.kibanaClient();

    logger.info("Setting default index");

    String url = "http://" + host + ":5601/api/kibana/settings";
    try {
      String requestBody = "{\"changes\":{\"defaultIndex\":\"syslog\"}}";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("kbn-version", context.config().targetVersion());
      HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
      Map<String, Object> res =
          kibanaClient.getRestTemplate().postForObject(url, requestEntity, Map.class);
      logger.info("Successfully set default index.");
      return TaskResult.success("Default index set.");
    } catch (Exception e) {
      logger.error("Failed to set default index.", e);
      return TaskResult.failure("Failed to set default index.");
    }

  }
}
