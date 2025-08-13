package co.hyperflex.dtos.prechecks;

public record GetPrecheckSummaryResponse(long critical, long warning, long skipped) {
}
