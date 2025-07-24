package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.Cluster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends MongoRepository<Cluster, String> {
}
