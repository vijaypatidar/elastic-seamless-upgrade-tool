package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.exceptions.NotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends MongoRepository<ClusterEntity, String> {

  default @NotNull ClusterEntity getCluster(String clusterId) {
    return findById(clusterId).orElseThrow(
        () -> new NotFoundException("Cluster not found with id: " + clusterId));
  }
}
