package co.hyperflex.services;

import co.hyperflex.services.notifications.NotificationService;
import co.hyperflex.ws.handlers.PrecheckWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RealtimeUpdateService {
  private static final Logger log = LoggerFactory.getLogger(RealtimeUpdateService.class);
  private final PrecheckWebSocketHandler webSocketHandler;
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  public RealtimeUpdateService(PrecheckWebSocketHandler webSocketHandler,
                               NotificationService notificationService, ObjectMapper objectMapper) {
    this.webSocketHandler = webSocketHandler;
    this.notificationService = notificationService;
    this.objectMapper = objectMapper;
  }

  @Deprecated
  public void notifyStatusChange(String precheckId, String status) {
    String payload =
        String.format("{\"precheckId\": \"%s\", \"status\": \"%s\"}", precheckId, status);
    webSocketHandler.sendMessageToAll(payload);
  }

  public void notifyStatusChange(String data) {
    webSocketHandler.sendMessageToAll(data);
  }

  @PostConstruct
  public void register() {
    notificationService.addNotificationListener(notification -> {
      try {
        String data = objectMapper.writeValueAsString(notification);
        notifyStatusChange(data);
      } catch (Exception e) {
        log.warn("Failed to register notification listener", e);
      }
    });
  }


}
