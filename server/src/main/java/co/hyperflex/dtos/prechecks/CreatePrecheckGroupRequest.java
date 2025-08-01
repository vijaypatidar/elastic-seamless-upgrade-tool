package co.hyperflex.dtos.prechecks;

public record CreatePrecheckGroupRequest(
    String clusterUpgradeJobId,
    String clusterId
) {
}
