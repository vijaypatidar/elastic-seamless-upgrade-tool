package co.hyperflex.dtos.prechecks;

import co.hyperflex.entities.precheck.PrecheckStatus;

public record GetPrecheckGroupResponse(String id,
                                       String clusterUpgradeJobId,
                                       String clusterId,
                                       PrecheckStatus status

) {
}
