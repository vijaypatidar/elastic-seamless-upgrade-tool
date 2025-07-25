package co.hyperflex.prechecks.runner;

import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.exceptions.NotFoundException;
import co.hyperflex.prechecks.contexts.ClusterContext;
import co.hyperflex.prechecks.contexts.IndexContext;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.contexts.PrecheckContext;
import co.hyperflex.prechecks.core.BaseClusterPrecheck;
import co.hyperflex.prechecks.core.BaseIndexPrecheck;
import co.hyperflex.prechecks.core.BaseNodePrecheck;
import co.hyperflex.prechecks.core.Precheck;
import co.hyperflex.prechecks.registry.PrecheckRegistry;
import co.hyperflex.repositories.PrecheckRunRepository;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class PrecheckRunner {
  private final PrecheckRunRepository precheckRunRepository;
  private final ExecutorService executor;
  private final PrecheckRegistry precheckRegistry;

  public PrecheckRunner(PrecheckRunRepository precheckRunRepository,
                        PrecheckRegistry precheckRegistry) {
    this.precheckRunRepository = precheckRunRepository;
    this.executor = Executors.newFixedThreadPool(10);
    this.precheckRegistry = precheckRegistry;
  }

  public void runNextBatch() {
    List<PrecheckRun> pending = precheckRunRepository.findTop20ByStatus(PrecheckStatus.PENDING);
    for (PrecheckRun precheckRun : pending) {
      executor.submit(() -> executeOne(precheckRun));
    }
  }

  private void executeOne(PrecheckRun record) {
    try {
      record.setStatus(PrecheckStatus.RUNNING);
      precheckRunRepository.save(record);

      PrecheckContext context = resolveContext(record);

      Precheck<?> precheck = precheckRegistry.getById(record.getPrecheckId())
          .orElseThrow(
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
        default -> throw new IllegalArgumentException("Unknown precheck type: " + record.getType());
      }

      record.setStatus(PrecheckStatus.PASSED);
      record.setEndAt(new Date());
      precheckRunRepository.save(record);

    } catch (Exception e) {
      record.setStatus(PrecheckStatus.PASSED);
      record.setEndAt(new Date());
      precheckRunRepository.save(record);
    }
  }

  private PrecheckContext resolveContext(PrecheckRun precheckRun) {
    //todo
    return null;
  }
}
