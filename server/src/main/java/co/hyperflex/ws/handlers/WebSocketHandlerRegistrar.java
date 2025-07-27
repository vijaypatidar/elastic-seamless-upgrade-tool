package co.hyperflex.ws.handlers;

import co.hyperflex.services.notifications.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebSocketHandlerRegistrar {
  private static final Logger log = LoggerFactory.getLogger(WebSocketHandlerRegistrar.class);
  private final PrecheckWebSocketHandler webSocketHandler;
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  public WebSocketHandlerRegistrar(PrecheckWebSocketHandler webSocketHandler,
                                   NotificationService notificationService,
                                   ObjectMapper objectMapper) {
    this.webSocketHandler = webSocketHandler;
    this.notificationService = notificationService;
    this.objectMapper = objectMapper;
  }


  @PostConstruct
  public void register() {
    notificationService.addNotificationListener(notification -> {
      try {
        String data = objectMapper.writeValueAsString(notification);
        webSocketHandler.sendMessageToAll(data);
      } catch (Exception e) {
        log.warn("Failed to register notification listener", e);
      }
    });
  }


}
