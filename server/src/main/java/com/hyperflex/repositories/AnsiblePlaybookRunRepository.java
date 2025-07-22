package com.hyperflex.repositories;

import com.hyperflex.entities.ansible.AnsiblePlaybookRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnsiblePlaybookRunRepository extends JpaRepository<AnsiblePlaybookRun, String> {
}
