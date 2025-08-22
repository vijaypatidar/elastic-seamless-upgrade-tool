package co.hyperflex.entities.cluster;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SELF_MANAGED")
public class SelfManagedClusterEntity extends ClusterEntity {

  private SshInfo sshInfo;

  public SelfManagedClusterEntity() {
    setType(ClusterType.SELF_MANAGED);
  }

  public SshInfo getSshInfo() {
    return sshInfo;
  }

  public void setSshInfo(SshInfo sshInfo) {
    this.sshInfo = sshInfo;
  }
}
