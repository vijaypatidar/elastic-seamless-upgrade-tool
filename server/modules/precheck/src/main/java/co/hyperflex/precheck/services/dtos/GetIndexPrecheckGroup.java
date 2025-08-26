package co.hyperflex.precheck.services.dtos;

import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.precheck.enums.PrecheckStatus;
import java.util.List;

public record GetIndexPrecheckGroup(
    String index,
    String name,
    PrecheckStatus status,
    PrecheckSeverity severity,
    List<GetPrecheckEntry> prechecks
) {
}
