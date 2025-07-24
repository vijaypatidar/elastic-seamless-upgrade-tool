package co.hyperflex.repositories;

import co.hyperflex.entities.ansible.AnsiblePlaybookRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnsiblePlaybookRunRepository extends JpaRepository<AnsiblePlaybookRun, String> {
}
