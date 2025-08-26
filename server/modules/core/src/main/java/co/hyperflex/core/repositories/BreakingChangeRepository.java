package co.hyperflex.core.repositories;

import co.hyperflex.core.entites.BreakingChangeEntity;
import co.hyperflex.core.utils.VersionUtils;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BreakingChangeRepository extends AbstractMongoRepository<BreakingChangeEntity, String> {

  protected BreakingChangeRepository(MongoTemplate mongoTemplate
  ) {
    super(mongoTemplate, BreakingChangeEntity.class);
  }

  public List<BreakingChangeEntity> getBreakingChanges(String currentVersion, String targetVersion) {
    return findAll()
        .stream()
        .filter(breakingChange -> VersionUtils.isVersionGt(breakingChange.getVersion(), currentVersion)
            && VersionUtils.isVersionGte(targetVersion, breakingChange.getVersion()))
        .toList();
  }
}
