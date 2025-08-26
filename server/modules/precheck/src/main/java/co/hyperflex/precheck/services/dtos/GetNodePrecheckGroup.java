package co.hyperflex.precheck.services.dtos;

import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.precheck.enums.PrecheckStatus;
import java.util.List;

public record GetNodePrecheckGroup(
    String nodeId,
    String ip,
    String name,
    PrecheckStatus status,
    PrecheckSeverity severity,
    List<GetPrecheckEntry> prechecks,
    int rank) {
}
