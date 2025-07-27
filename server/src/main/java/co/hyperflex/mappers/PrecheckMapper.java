package co.hyperflex.mappers;

import co.hyperflex.dtos.prechecks.GetGroupedPrecheckResponseModels;
import co.hyperflex.entities.precheck.PrecheckRun;
import org.springframework.stereotype.Service;

@Service
public class PrecheckMapper {

  public GetGroupedPrecheckResponseModels.GetPrecheckEntry toPrecheckEntry(
      PrecheckRun precheckRun) {
    long duration = 0;
    if (precheckRun.getStartedAt() != null && precheckRun.getEndAt() != null) {
      duration = precheckRun.getEndAt().getTime() - precheckRun.getStartedAt().getTime();
    }
    return new GetGroupedPrecheckResponseModels.GetPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getLogs(),
        duration
    );
  }

  public GetGroupedPrecheckResponseModels.GetClusterPrecheckEntry toClusterPrecheckEntry(
      PrecheckRun precheckRun) {
    long duration = 0;
    if (precheckRun.getStartedAt() != null && precheckRun.getEndAt() != null) {
      duration = precheckRun.getEndAt().getTime() - precheckRun.getStartedAt().getTime();
    }
    return new GetGroupedPrecheckResponseModels.GetClusterPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getLogs(),
        duration
    );
  }

}
