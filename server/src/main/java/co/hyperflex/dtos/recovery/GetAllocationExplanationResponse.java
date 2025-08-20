package co.hyperflex.dtos.recovery;

public record GetAllocationExplanationResponse(
    String index,
    String shard,
    String explanation) {
}
