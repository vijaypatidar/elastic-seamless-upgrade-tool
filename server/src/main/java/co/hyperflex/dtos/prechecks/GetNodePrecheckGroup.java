package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckStatus;
import java.util.List;

public record GetNodePrecheckGroup(
    String nodeId,
    String ip,
    String name,
    PrecheckStatus status,
    List<GetPrecheckEntry> prechecks
) {
}
