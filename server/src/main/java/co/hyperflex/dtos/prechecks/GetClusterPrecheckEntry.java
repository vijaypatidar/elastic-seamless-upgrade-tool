package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckStatus;
import java.util.List;

public record GetClusterPrecheckEntry(
    String id,
    String name,
    PrecheckStatus status,
    PrecheckSeverity severity,
    List<String> logs,
    double duration
) {
}
