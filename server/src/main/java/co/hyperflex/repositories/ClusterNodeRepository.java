package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.ClusterNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeRepository extends MongoRepository<ClusterNode, String> {
}
