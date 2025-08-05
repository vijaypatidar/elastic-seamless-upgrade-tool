package co.hyperflex.prechecks.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import co.hyperflex.services.PrecheckRunService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class PrecheckContextAwareAppender extends AppenderBase<ILoggingEvent> {
  private final PrecheckRunService precheckRunService;

  public PrecheckContextAwareAppender(PrecheckRunService precheckRunService) {
    this.precheckRunService = precheckRunService;
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    String precheckRunId = MDC.get("precheckRunId");
    String message = eventObject.getFormattedMessage();
    precheckRunService.addLog(precheckRunId, message);
  }
}