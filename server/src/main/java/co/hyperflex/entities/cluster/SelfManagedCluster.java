package co.hyperflex.entities.cluster;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SELF_MANAGED")
public class SelfManagedCluster extends Cluster {

  private SshInfo sshInfo;

  public SelfManagedCluster() {
    setType(ClusterType.SELF_MANAGED);
  }

  public SshInfo getSshInfo() {
    return sshInfo;
  }

  public void setSshInfo(SshInfo sshInfo) {
    this.sshInfo = sshInfo;
  }
}
