package co.hyperflex.prechecks.runner;

import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.repositories.PrecheckRunRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrecheckRunner {
  private static final Logger LOG = LoggerFactory.getLogger(PrecheckRunner.class);
  private final PrecheckRunRepository precheckRunRepository;
  private final PrecheckTaskExecutor taskExecutor;

  public PrecheckRunner(PrecheckRunRepository precheckRunRepository,
                        PrecheckTaskExecutor taskExecutor) {
    this.precheckRunRepository = precheckRunRepository;
    this.taskExecutor = taskExecutor;
  }

  @Scheduled(fixedDelay = 5000)
  public void runNextBatch() {
    List<PrecheckRun> pending = precheckRunRepository.findTop40ByStatus(PrecheckStatus.PENDING);
    if (pending.isEmpty()) {
      LOG.debug("No pending precheck runs found");
      return;
    }

    LOG.info("Found {} precheck runs", pending.size());

    List<CompletableFuture<Void>> futures = pending.stream()
        .map(taskExecutor::executeOne)
        .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }
}
