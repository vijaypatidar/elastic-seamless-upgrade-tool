package com.hyperflex.repositories;

import com.hyperflex.entities.cluster.ClusterNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeRepository extends JpaRepository<ClusterNode, String> {
}
