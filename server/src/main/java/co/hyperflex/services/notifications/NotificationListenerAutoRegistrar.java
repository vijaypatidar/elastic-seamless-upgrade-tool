package co.hyperflex.services.notifications;

import co.hyperflex.services.SettingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationListenerAutoRegistrar {
  private final Logger logger = LoggerFactory.getLogger(NotificationListenerAutoRegistrar.class);

  public NotificationListenerAutoRegistrar(NotificationService notificationService, SettingService settingService) {
    RestClient restClient = RestClient.builder().build();
    notificationService.addNotificationListener(notification -> {
      if (notification instanceof GeneralNotificationEvent) {
        Optional.ofNullable(settingService.getSetting().notificationWebhookUrl()).ifPresent(notificationWebhookUrl -> {
          try {
            restClient.post().uri(notificationWebhookUrl).body(notification).retrieve().body(String.class);
          } catch (Exception e) {
            logger.error("Error posting webhook url", e);
          }
        });
      }
    });

  }

}
