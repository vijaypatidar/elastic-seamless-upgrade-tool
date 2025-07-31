package co.hyperflex.repositories;

import co.hyperflex.entities.BreakingChange;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakingChangeRepository extends MongoRepository<BreakingChange, String> {
}
