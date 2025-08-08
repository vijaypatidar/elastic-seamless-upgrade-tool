package co.hyperflex.mappers;

import co.hyperflex.dtos.prechecks.GetClusterPrecheckEntry;
import co.hyperflex.dtos.prechecks.GetPrecheckEntry;
import co.hyperflex.entities.precheck.PrecheckRun;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PrecheckMapper {

  public GetPrecheckEntry toPrecheckEntry(
      PrecheckRun precheckRun) {

    return new GetPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getSeverity(),
        precheckRun.getLogs(),
        getDuration(precheckRun.getStartTime(), precheckRun.getEndTime())
    );
  }

  public GetClusterPrecheckEntry toClusterPrecheckEntry(
      PrecheckRun precheckRun) {

    return new GetClusterPrecheckEntry(
        precheckRun.getId(),
        precheckRun.getName(),
        precheckRun.getStatus(),
        precheckRun.getSeverity(),
        precheckRun.getLogs(),
        getDuration(precheckRun.getStartTime(), precheckRun.getEndTime())
    );
  }

  private String getDuration(Date start, Date end) {
    double duration = 0.0;
    if (start != null) {
      duration =
          (Optional.ofNullable(end).orElse(new Date()).getTime() - start.getTime()) / 1000.0;
    }
    return String.format("%.1f", duration) + "s";
  }

}
