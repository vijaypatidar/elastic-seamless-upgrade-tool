package com.hyperflex.ansible;

import com.hyperflex.entities.ansible.AnsibleLogEntry;
import com.hyperflex.entities.ansible.AnsiblePlaybookRun;
import com.hyperflex.repositories.AnsibleLogEntryRepository;
import com.hyperflex.repositories.AnsiblePlaybookRunRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AnsibleLoggingServiceImpl implements AnsibleLoggingService {
  private final AnsibleLogEntryRepository ansibleLogEntryRepository;
  private final AnsiblePlaybookRunRepository ansiblePlaybookRunRepository;

  public AnsibleLoggingServiceImpl(AnsibleLogEntryRepository ansibleLogEntryRepository,
                                   AnsiblePlaybookRunRepository ansiblePlaybookRunRepository) {
    this.ansibleLogEntryRepository = ansibleLogEntryRepository;
    this.ansiblePlaybookRunRepository = ansiblePlaybookRunRepository;
  }


  @Override
  public void logEvent(AnsibleLogEvent event) {
    AnsibleLogEntry ansibleLogEntry = new AnsibleLogEntry();
    ansibleLogEntry.setMessage(event.message());
    Optional<AnsiblePlaybookRun> ansiblePlaybookRunOptional =
        ansiblePlaybookRunRepository.findById(event.playbookRunId());
    ansiblePlaybookRunOptional.ifPresent(ansiblePlaybookRun -> {
      ansibleLogEntry.setPlaybookRun(ansiblePlaybookRun);
      ansibleLogEntryRepository.save(ansibleLogEntry);
    });
  }
}
