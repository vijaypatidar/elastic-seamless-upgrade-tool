package co.hyperflex.prechecks.concrete.node.os;

import co.hyperflex.ansible.AnsibleService;
import co.hyperflex.ansible.commands.AnsibleAdHocShellCommand;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import co.hyperflex.entities.cluster.SshInfo;
import co.hyperflex.prechecks.contexts.NodeContext;
import co.hyperflex.prechecks.core.BaseNodePrecheck;
import java.util.function.Consumer;
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
      ansibleService.run(cmd, new Consumer<String>() {
        @Override
        public void accept(String s) {
          context.getLogger().info(s);
        }
      }, new Consumer<String>() {
        @Override
        public void accept(String s) {
          context.getLogger().error(s);
        }
      });
    }
  }
}
