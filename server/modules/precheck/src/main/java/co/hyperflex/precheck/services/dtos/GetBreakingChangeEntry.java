package co.hyperflex.precheck.services.dtos;

import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.precheck.enums.PrecheckStatus;
import java.util.List;

public record GetBreakingChangeEntry(
    String id,
    String name,
    List<String> logs,
    PrecheckSeverity severity,
    PrecheckStatus status
) {
}
