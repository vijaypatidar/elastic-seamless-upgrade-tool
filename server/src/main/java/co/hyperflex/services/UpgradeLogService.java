package co.hyperflex.services;

import co.hyperflex.dtos.upgrades.GetUpgradeLogsRequest;
import co.hyperflex.dtos.upgrades.GetUpgradeLogsResponse;
import co.hyperflex.entities.upgrade.UpgradeLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class UpgradeLogService {
  private final MongoTemplate mongoTemplate;

  public UpgradeLogService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public void addLog(String jobId, String nodeId, String message) {
    Query query = new Query(Criteria.where("clusterUpgradeJobId").is(jobId).and("nodeId").is(nodeId));
    Update update = new Update().push("logs", "[" + LocalDateTime.now() + "] " + message).setOnInsert("clusterUpgradeJobId", jobId)
        .setOnInsert("nodeId", nodeId);
    mongoTemplate.upsert(query, update, UpgradeLog.class);
  }

  public GetUpgradeLogsResponse getLogs(GetUpgradeLogsRequest request) {
    Query query = new Query(Criteria.where("nodeId").is(request.nodeId()));
    UpgradeLog upgradeLog = mongoTemplate.findOne(query, UpgradeLog.class);
    return new GetUpgradeLogsResponse(Optional.ofNullable(upgradeLog).map(UpgradeLog::getLogs).orElse(List.of()));
  }

}

