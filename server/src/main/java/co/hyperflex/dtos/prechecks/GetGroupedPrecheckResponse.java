package co.hyperflex.dtos.prechecks;

import java.util.List;

public record GetGroupedPrecheckResponse(
    List<GetNodePrecheckGroup> node,
    List<GetClusterPrecheckEntry> cluster,
    List<GetIndexPrecheckGroup> index
) {
}
