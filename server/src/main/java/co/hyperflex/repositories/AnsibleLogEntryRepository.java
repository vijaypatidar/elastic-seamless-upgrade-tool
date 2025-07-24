package co.hyperflex.repositories;

import co.hyperflex.entities.ansible.AnsibleLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnsibleLogEntryRepository extends JpaRepository<AnsibleLogEntry, Long> {
}
