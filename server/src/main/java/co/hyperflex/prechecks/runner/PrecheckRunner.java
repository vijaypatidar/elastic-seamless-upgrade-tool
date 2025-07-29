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
import co.hyperflex.repositories.PrecheckRunRepository;
import co.hyperflex.services.notifications.NotificationService;
import co.hyperflex.services.notifications.PrecheckProgressChangeEvent;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PrecheckRunner {
  private static final Logger LOG = LoggerFactory.getLogger(PrecheckRunner.class);
  private final PrecheckRunRepository precheckRunRepository;
  private final ExecutorService executor;
  private final PrecheckRegistry precheckRegistry;
  private final PrecheckContextResolver precheckContextResolver;
  private final NotificationService notificationService;

  public PrecheckRunner(PrecheckRunRepository precheckRunRepository,
                        PrecheckRegistry precheckRegistry,
                        PrecheckContextResolver precheckContextResolver,
                        NotificationService notificationService) {
    this.precheckRunRepository = precheckRunRepository;
    this.precheckContextResolver = precheckContextResolver;
    this.notificationService = notificationService;
    this.executor = Executors.newFixedThreadPool(4);
    this.precheckRegistry = precheckRegistry;
  }

  @PostConstruct
  public void runNextBatch() {
    new Thread(() -> {
      while (true) {

        List<PrecheckRun> pendingPrecheckRuns =
            precheckRunRepository.findTop40ByStatus(PrecheckStatus.PENDING);
        if (pendingPrecheckRuns.isEmpty()) {
          LOG.debug("No pending precheck runs found");
          try {
            Thread.sleep(5000);
            continue;
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        CountDownLatch latch = new CountDownLatch(pendingPrecheckRuns.size());

        for (PrecheckRun precheckRun : pendingPrecheckRuns) {
          executor.submit(() -> {
            try {
              executeOne(precheckRun);
            } finally {
              latch.countDown(); // Mark task as done
            }
          });
        }

        try {
          latch.await(); // Wait for all tasks in this batch to finish
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }).start();
  }


  private void executeOne(PrecheckRun record) {
    try {
      record.setStatus(PrecheckStatus.RUNNING);
      record.setStartedAt(new Date());
      record.getLogs().clear();
      precheckRunRepository.save(record);
      notificationService.sendNotification(new PrecheckProgressChangeEvent());
      PrecheckContext context = precheckContextResolver.resolveContext(record);

      try {
        Precheck<?> precheck = precheckRegistry.getById(record.getPrecheckId()).orElseThrow(
            () -> new NotFoundException("Precheck not found: " + record.getPrecheckId()));
        switch (record.getType()) {
          case NODE -> {
            if (precheck instanceof BaseNodePrecheck nodePrecheck) {
              nodePrecheck.run((NodeContext) context);
            } else {
              throw new IllegalStateException(
                  "Precheck is not a NodePrecheck: " + precheck.getClass());
            }
          }
          case INDEX -> {
            if (precheck instanceof BaseIndexPrecheck indexPrecheck) {
              indexPrecheck.run((IndexContext) context);
            } else {
              throw new IllegalStateException(
                  "Precheck is not an IndexPrecheck: " + precheck.getClass());
            }
          }
          case CLUSTER -> {
            if (precheck instanceof BaseClusterPrecheck clusterPrecheck) {
              clusterPrecheck.run((ClusterContext) context);
            } else {
              throw new IllegalStateException(
                  "Precheck is not a ClusterPrecheck: " + precheck.getClass());
            }
          }
          default ->
              throw new IllegalArgumentException("Unknown precheck type: " + record.getType());
        }
        record.setStatus(PrecheckStatus.COMPLETED);
        record.setEndAt(new Date());
        precheckRunRepository.save(record);
      } finally {
        context.getElasticClient().getElasticsearchClient().close();
      }

    } catch (Exception e) {
      record.setStatus(PrecheckStatus.FAILED);
      record.setEndAt(new Date());
      record.getLogs().add(e.getMessage());
      precheckRunRepository.save(record);
    } finally {
      notificationService.sendNotification(new PrecheckProgressChangeEvent());
    }
  }
}
