package co.hyperflex.prechecks.runner;

import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.contexts.PrecheckContext;
import co.hyperflex.prechecks.contexts.resolver.PrecheckContextResolver;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import co.hyperflex.prechecks.core.BaseNodePrecheck;
import co.hyperflex.prechecks.core.Precheck;
import co.hyperflex.prechecks.registry.PrecheckRegistry;
import co.hyperflex.services.PrecheckRunService;
import co.hyperflex.services.notifications.NotificationService;
import co.hyperflex.services.notifications.PrecheckProgressChangeEvent;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PrecheckTaskExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(PrecheckTaskExecutor.class);

  private final PrecheckRegistry precheckRegistry;
  private final PrecheckContextResolver precheckContextResolver;
  private final NotificationService notificationService;
  private final PrecheckRunService precheckRunService;

  public PrecheckTaskExecutor(PrecheckRegistry precheckRegistry,
                              PrecheckContextResolver precheckContextResolver, NotificationService notificationService,
                              PrecheckRunService precheckRunService) {
    this.precheckRegistry = precheckRegistry;
    this.precheckContextResolver = precheckContextResolver;
    this.notificationService = notificationService;
    this.precheckRunService = precheckRunService;
  }

  @Async("precheckAsyncExecutor")
  public CompletableFuture<Void> executeOne(PrecheckRun record) {
    try {
      MDC.put("precheckRunId", record.getId());
      precheckRunService.updatePrecheckStatus(record.getId(), PrecheckStatus.RUNNING);
      notificationService.sendNotification(new PrecheckProgressChangeEvent());

      PrecheckContext context = precheckContextResolver.resolveContext(record);
      Precheck<?> precheck = precheckRegistry.getById(record.getPrecheckId())
          .orElseThrow(() -> new NotFoundException("Precheck not found: " + record.getPrecheckId()));

      switch (record.getType()) {
        case NODE -> ((BaseNodePrecheck) precheck).run((NodeContext) context);
        case INDEX -> ((BaseIndexPrecheck) precheck).run((IndexContext) context);
        case CLUSTER -> ((BaseClusterPrecheck) precheck).run((ClusterContext) context);
        default -> throw new IllegalArgumentException("Unknown precheck type: " + record.getType());
      }
      precheckRunService.updatePrecheckStatus(record.getId(), PrecheckStatus.COMPLETED);

    } catch (Exception e) {
      LOG.error("Error executing precheck: {}", record.getId(), e);
      precheckRunService.updatePrecheckStatus(record.getId(), PrecheckStatus.FAILED);
    } finally {
      notificationService.sendNotification(new PrecheckProgressChangeEvent());
      MDC.clear();
    }
    return CompletableFuture.completedFuture(null);
  }

}
