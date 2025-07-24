package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeRepository extends MongoRepository<ClusterNode, String> {
  List<ClusterNode> findByClusterId(String clusterId);

  List<ClusterNode> findByClusterIdAndType(String clusterId, ClusterNodeType type);
}
