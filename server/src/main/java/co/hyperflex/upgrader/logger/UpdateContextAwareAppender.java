package co.hyperflex.upgrader.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import co.hyperflex.services.UpgradeLogService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class UpdateContextAwareAppender extends AppenderBase<ILoggingEvent> {
  private final UpgradeLogService upgradeLogService;

  public UpdateContextAwareAppender(UpgradeLogService upgradeLogService) {
    this.upgradeLogService = upgradeLogService;
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    String nodeId = MDC.get("nodeId");
    String clusterUpgradeJobId = MDC.get("clusterUpgradeJobId");
    String message = eventObject.getFormattedMessage();
    upgradeLogService.addLog(clusterUpgradeJobId, nodeId, message);
  }
}