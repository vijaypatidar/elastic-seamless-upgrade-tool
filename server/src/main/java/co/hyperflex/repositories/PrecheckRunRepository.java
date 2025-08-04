package co.hyperflex.repositories;

import co.hyperflex.entities.precheck.PrecheckRun;
import co.hyperflex.entities.precheck.PrecheckStatus;
import co.hyperflex.repositories.projection.PrecheckStatusAndSeverityView;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class PrecheckRunRepository {

    private final MongoTemplate mongoTemplate;

    public PrecheckRunRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <S extends PrecheckRun> S save(S entity) {
        return mongoTemplate.save(entity);
    }

    public <S extends PrecheckRun> List<S> saveAll(Iterable<S> entities) {
        List<S> entityList = new ArrayList<>();
        entities.forEach(entityList::add);
        Collection<S> savedEntities = mongoTemplate.insertAll(entityList);
        return new ArrayList<>(savedEntities);
    }

    public List<PrecheckRun> findTop40ByStatus(PrecheckStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(status));
        query.limit(40);
        return mongoTemplate.find(query, PrecheckRun.class);
    }

    public List<PrecheckRun> findByPrecheckGroupId(String precheckGroupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("precheckGroupId").is(precheckGroupId));
        return mongoTemplate.find(query, PrecheckRun.class);
    }

    public List<PrecheckStatusAndSeverityView> findStatusAndSeverityByPrecheckGroupId(String precheckGroupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("precheckGroupId").is(precheckGroupId));
        query.fields().include("status").include("severity");
        List<PrecheckRun> results = mongoTemplate.find(query, PrecheckRun.class);
        return results.stream()
                .map(pr -> new PrecheckStatusAndSeverityView(pr.getStatus(), pr.getSeverity()))
                .toList();
    }
}