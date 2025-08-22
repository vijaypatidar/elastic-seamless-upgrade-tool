package co.hyperflex.services;

import co.hyperflex.dtos.upgrades.GetUpgradeLogsRequest;
import co.hyperflex.dtos.upgrades.GetUpgradeLogsResponse;
import co.hyperflex.entities.upgrade.ClusterUpgradeJobEntity;
import co.hyperflex.entities.upgrade.UpgradeLogEntity;
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
  private final ClusterUpgradeJobService clusterUpgradeJobService;

  public UpgradeLogService(MongoTemplate mongoTemplate, ClusterUpgradeJobService clusterUpgradeJobService) {
    this.mongoTemplate = mongoTemplate;
    this.clusterUpgradeJobService = clusterUpgradeJobService;
  }

  public void addLog(String jobId, String nodeId, String message) {
    Query query = new Query(Criteria.where(UpgradeLogEntity.CLUSTER_UPGRADE_JOB_ID).is(jobId).and(UpgradeLogEntity.NODE_ID).is(nodeId));
    Update update = new Update()
        .push(UpgradeLogEntity.LOGS, "[" + LocalDateTime.now() + "] " + message)
        .setOnInsert(UpgradeLogEntity.CLUSTER_UPGRADE_JOB_ID, jobId)
        .setOnInsert(UpgradeLogEntity.NODE_ID, nodeId);
    mongoTemplate.upsert(query, update, UpgradeLogEntity.class);
  }

  public GetUpgradeLogsResponse getLogs(GetUpgradeLogsRequest request) {
    ClusterUpgradeJobEntity job = clusterUpgradeJobService.getLatestJobByClusterId(request.clusterId());
    Query query = new Query(
        Criteria.where(UpgradeLogEntity.NODE_ID).is(request.nodeId())
            .and(UpgradeLogEntity.CLUSTER_UPGRADE_JOB_ID).is(job.getId()));
    UpgradeLogEntity upgradeLog = mongoTemplate.findOne(query, UpgradeLogEntity.class);
    return new GetUpgradeLogsResponse(Optional.ofNullable(upgradeLog).map(UpgradeLogEntity::getLogs).orElse(List.of()));
  }

}

