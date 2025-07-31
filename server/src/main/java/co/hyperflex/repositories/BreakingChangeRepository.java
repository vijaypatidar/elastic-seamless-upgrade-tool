package co.hyperflex.repositories;

import co.hyperflex.entities.BreakingChange;
import co.hyperflex.utils.VersionUtils;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakingChangeRepository extends MongoRepository<BreakingChange, String> {

  default List<BreakingChange> getBreakingChanges(String currentVersion, String targetVersion) {
    return findAll()
        .stream()
        .filter(breakingChange -> VersionUtils.isVersionGt(breakingChange.getVersion(), currentVersion)
            && VersionUtils.isVersionGte(targetVersion, breakingChange.getVersion()))
        .toList();
  }
}
