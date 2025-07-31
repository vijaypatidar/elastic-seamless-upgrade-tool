package co.hyperflex.mappers;

import co.hyperflex.dtos.prechecks.GetClusterPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetPrecheckEntry;
import co.hyperflex.entities.precheck.PrecheckRun;
import org.springframework.stereotype.Service;

@Service
public class PrecheckMapper {

  public GetPrecheckEntry toPrecheckEntry(
      PrecheckRun precheckRun) {
    long duration = 0;
    if (precheckRun.getStartedAt() != null && precheckRun.getEndAt() != null) {
      duration = precheckRun.getEndAt().getTime() - precheckRun.getStartedAt().getTime();
    }
    return new GetPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getSeverity(),
        precheckRun.getLogs(),
        duration + "ms"
    );
  }

  public GetClusterPrecheckEntry toClusterPrecheckEntry(
      PrecheckRun precheckRun) {
    long duration = 0;
    if (precheckRun.getStartedAt() != null && precheckRun.getEndAt() != null) {
      duration = precheckRun.getEndAt().getTime() - precheckRun.getStartedAt().getTime();
    }
    return new GetClusterPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getSeverity(),
        precheckRun.getLogs(),
        duration
    );
  }

}
