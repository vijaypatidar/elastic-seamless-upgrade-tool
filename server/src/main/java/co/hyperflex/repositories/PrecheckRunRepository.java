package co.hyperflex.repositories;

import static co.hyperflex.entities.precheck.PrecheckRun.CLUSTER_UPGRADE_JOB_ID;
import static co.hyperflex.entities.precheck.PrecheckRun.LOGS;
import static co.hyperflex.entities.precheck.PrecheckRun.SEVERITY;
import static co.hyperflex.entities.precheck.PrecheckRun.STATUS;

import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.repositories.projection.PrecheckStatusAndSeverityView;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class PrecheckRunRepository extends AbstractMongoRepository<PrecheckRun, String> {

  public PrecheckRunRepository(MongoTemplate mongoTemplate) {
    super(mongoTemplate, PrecheckRun.class);
  }

  public List<PrecheckRun> getPendingPrechecks() {
    Query query = new Query();
    query.addCriteria(Criteria.where(STATUS).is(PrecheckStatus.PENDING));
    query.limit(40);
    return mongoTemplate.find(query, PrecheckRun.class, collectionName);
  }

  public List<PrecheckRun> getAllByJobId(String upgradeJobId) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CLUSTER_UPGRADE_JOB_ID).is(upgradeJobId));
    return mongoTemplate.find(query, PrecheckRun.class, collectionName);
  }

  public List<PrecheckStatusAndSeverityView> findStatusAndSeverityByUpgradeJobId(String upgradeJobId) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CLUSTER_UPGRADE_JOB_ID).is(upgradeJobId));
    query.fields().include(STATUS).include(SEVERITY);
    return mongoTemplate.find(query, PrecheckStatusAndSeverityView.class, collectionName);
  }

  public void addLog(String precheckRunId, String message) {
    Update update = new Update().push(LOGS, message);
    updateById(precheckRunId, update);
  }

  public int getCountByJobId(String upgradeJobId) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CLUSTER_UPGRADE_JOB_ID).is(upgradeJobId));
    return mongoTemplate.find(query, PrecheckRun.class, collectionName).size();
  }
}