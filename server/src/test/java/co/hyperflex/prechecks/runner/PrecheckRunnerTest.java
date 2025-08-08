package co.hyperflex.prechecks.runner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import co.hyperflex.entities.precheck.ClusterPrecheckRun;
import co.hyperflex.entities.precheck.NodePrecheckRun;
import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.services.PrecheckRunService;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrecheckRunnerTest {

  @Mock
  private PrecheckTaskExecutor taskExecutor;

  @Mock
  private PrecheckRunService precheckRunService;

  @InjectMocks
  private PrecheckRunner precheckRunner;

  @Test
  void runNextBatch_withPendingPrechecks_executesTasks() {
    // Arrange
    PrecheckRun run1 = new ClusterPrecheckRun();
    PrecheckRun run2 = new NodePrecheckRun();
    List<PrecheckRun> pendingRuns = List.of(run1, run2);

    when(precheckRunService.getPendingPrechecks()).thenReturn(pendingRuns);
    when(taskExecutor.executeOne(any(PrecheckRun.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    // Act
    precheckRunner.runNextBatch();

    // Assert
    verify(precheckRunService).getPendingPrechecks();
    verify(taskExecutor).executeOne(run1);
    verify(taskExecutor).executeOne(run2);
    verifyNoMoreInteractions(taskExecutor);
  }

  @Test
  void runNextBatch_withNoPendingPrechecks_doesNothing() {
    // Arrange
    when(precheckRunService.getPendingPrechecks()).thenReturn(Collections.emptyList());

    // Act
    precheckRunner.runNextBatch();

    // Assert
    verify(precheckRunService).getPendingPrechecks();
    verifyNoInteractions(taskExecutor);
  }
}
