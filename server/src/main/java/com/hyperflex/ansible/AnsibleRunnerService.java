package com.hyperflex.ansible;

import java.util.concurrent.CompletableFuture;

/**
 * A service for executing Ansible playbooks asynchronously.
 */
public interface AnsibleRunnerService {

  /**
   * Executes an Ansible playbook asynchronously.
   *
   * @param request The request object containing all necessary details for the run.
   * @return A CompletableFuture holding the final exit code of the Ansible process.
   *     0 typically indicates success. The future will complete exceptionally on process
   *     creation errors (e.g., playbook not found).
   */
  CompletableFuture<Integer> executePlaybook(final AnsibleRunRequest request);

}