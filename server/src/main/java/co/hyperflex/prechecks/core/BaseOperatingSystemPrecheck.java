package co.hyperflex.prechecks.core;

import co.hyperflex.ansible.AnsibleService;

public abstract class BaseOperatingSystemPrecheck extends BaseNodePrecheck {
  private final AnsibleService ansibleService;

  protected BaseOperatingSystemPrecheck(AnsibleService ansibleService) {
    this.ansibleService = ansibleService;
  }

}
