package co.hyperflex.repositories;

import co.hyperflex.core.models.enums.ClusterNodeType;
import co.hyperflex.entities.cluster.ClusterNodeEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeRepository extends MongoRepository<ClusterNodeEntity, String> {
  List<ClusterNodeEntity> findByClusterId(String clusterId);

  List<ClusterNodeEntity> findByClusterIdAndType(String clusterId, ClusterNodeType type);
}
