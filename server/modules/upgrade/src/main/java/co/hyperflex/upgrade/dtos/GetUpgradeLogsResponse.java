package co.hyperflex.upgrade.dtos;

import java.util.List;

public record GetUpgradeLogsResponse(
    List<String> logs
) {
}
