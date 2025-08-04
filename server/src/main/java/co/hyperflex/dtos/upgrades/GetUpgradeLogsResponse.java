package co.hyperflex.dtos.upgrades;

import java.util.List;

public record GetUpgradeLogsResponse(
    List<String> logs
) {
}
