package co.hyperflex.dtos;

import java.util.List;

public record GetDeprecationsResponse(
    String issue,
    String issueDetails,
    String type,
    List<String> resolutions
) {
}
