package co.hyperflex.dtos;

public record GetElasticNodeAndIndexCountsResponse(
    int dataNodes,
    int totalNodes,
    int masterNodes,
    int totalIndices,
    int activePrimaryShards,
    int activeShards,
    int unassignedShards,
    int initializingShards,
    int relocatingShards
) {
}
