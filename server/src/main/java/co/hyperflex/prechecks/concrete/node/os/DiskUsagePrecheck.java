package co.hyperflex.prechecks.concrete.node.os;

import co.hyperflex.ansible.AnsibleAdHocCommandResult;
import co.hyperflex.ansible.AnsibleService;
import co.hyperflex.ansible.commands.AnsibleAdHocShellCommand;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import co.hyperflex.entities.cluster.SshInfo;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseNodePrecheck;
import org.springframework.stereotype.Component;

@Component
public class DiskUsagePrecheck extends BaseNodePrecheck {
  private final AnsibleService ansibleService;

  public DiskUsagePrecheck(AnsibleService ansibleService) {
    this.ansibleService = ansibleService;
  }

  @Override
  public String getName() {
    return "Disk Utilization Check";
  }

  @Override
  public void run(NodeContext context) {
    if (context.getCluster() instanceof SelfManagedCluster selfManagedCluster) {
      SshInfo sshInfo = selfManagedCluster.getSshInfo();

      AnsibleAdHocShellCommand cmd = new AnsibleAdHocShellCommand.Builder().hostIp(context.getNode().getIp())
          .args("df -h / | tail -n 1 | awk '{ print $5 }' | tr -d %").sshUsername(sshInfo.username()).sshKeyPath(sshInfo.keyPath())
          .useBecome(true).build();
      AnsibleAdHocCommandResult result = ansibleService.run(cmd);

      if (result.success()) {
        context.getLogger().info("Disk Utilization check completed");
        context.getLogger().info("Disk Utilized: {}%", result.stdOutLogs().getLast());
      } else {
        context.getLogger().info("Disk Utilization check failed");
        result.stdOutLogs().forEach(context.getLogger()::error);
        throw new RuntimeException("Unable to run Disk Utilization check");
      }
    }
  }
}
