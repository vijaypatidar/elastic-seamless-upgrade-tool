package co.hyperflex.repositories;

import co.hyperflex.entities.cluster.ClusterNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeRepository extends JpaRepository<ClusterNode, String> {
}
