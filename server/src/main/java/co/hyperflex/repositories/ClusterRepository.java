package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, String> {
}
