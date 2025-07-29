package co.hyperflex.services.notifications;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
  private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
  private final List<NotificationListener> listeners = new LinkedList<>();
  private final RestTemplate restTemplate;
  private final String seamlessLegacyUrl;

  public NotificationService(RestTemplate restTemplate,
                             @Value("${seamless.legacy-backend}") String seamlessLegacyUrl) {
    this.restTemplate = restTemplate;
    this.seamlessLegacyUrl = seamlessLegacyUrl;
  }

  public void sendNotification(NotificationEvent notification) {
    for (NotificationListener listener : listeners) {
      try {
        listener.onNotification(notification);
      } catch (Exception e) {
        log.warn(e.getMessage());
      }
    }
    try {
      restTemplate.postForEntity(seamlessLegacyUrl + "/realtime-update", notification, Void.class);
    } catch (Exception e) {
      log.warn(e.getMessage());
    }
  }

  public void addNotificationListener(NotificationListener listener) {
    listeners.add(listener);
  }

  public void removeNotificationListener(NotificationListener listener) {
    listeners.removeIf(existing -> existing == listener);
  }

  public interface NotificationListener {
    void onNotification(NotificationEvent notification);
  }
}
