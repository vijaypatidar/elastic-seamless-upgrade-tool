package co.hyperflex.repositories;

import co.hyperflex.entities.SettingEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SettingRepository extends AbstractMongoRepository<SettingEntity, String> {
  protected SettingRepository(MongoTemplate mongoTemplate) {
    super(mongoTemplate, SettingEntity.class);
  }
}
