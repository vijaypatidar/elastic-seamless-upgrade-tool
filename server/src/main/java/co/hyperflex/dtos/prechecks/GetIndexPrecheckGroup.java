package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckStatus;
import java.util.List;

public record GetIndexPrecheckGroup(
    String index,
    String name,
    PrecheckStatus status,
    List<GetPrecheckEntry> prechecks
) {
}
