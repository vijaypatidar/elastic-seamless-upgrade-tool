package co.hyperflex.upgrade.services;

import co.hyperflex.upgrade.dtos.GetUpgradeLogsRequest;
import co.hyperflex.upgrade.dtos.GetUpgradeLogsResponse;

public interface UpgradeLogService {
  void addLog(String jobId, String nodeId, String message);

  GetUpgradeLogsResponse getLogs(GetUpgradeLogsRequest request);
}
