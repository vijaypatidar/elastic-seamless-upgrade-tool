package co.hyperflex.repositories;

import co.hyperflex.entities.SettingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends MongoRepository<SettingEntity, String> {
}
