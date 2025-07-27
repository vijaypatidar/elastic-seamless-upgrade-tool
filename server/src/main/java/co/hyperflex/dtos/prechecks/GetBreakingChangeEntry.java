package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckStatus;
import java.util.List;

public record GetBreakingChangeEntry(
      String id,
      String name,
      List<String> logs,
      PrecheckStatus status
) {
}
