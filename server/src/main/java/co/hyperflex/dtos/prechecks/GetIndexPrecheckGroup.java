package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckStatus;
import java.util.List;

public record GetIndexPrecheckGroup(
    String index,
    String name,
    PrecheckStatus status,
    PrecheckSeverity severity,
    List<GetPrecheckEntry> prechecks
) {
}
