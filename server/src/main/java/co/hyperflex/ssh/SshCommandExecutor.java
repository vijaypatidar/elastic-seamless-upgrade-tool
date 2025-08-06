package co.hyperflex.ssh;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.security.SecurityUtils;

public class SshCommandExecutor implements AutoCloseable {

  private final SshClient client;
  private final ClientSession session;
  private final long timeoutSeconds;

  public SshCommandExecutor(String host, int port, String username, String privateKeyPath, long timeoutSeconds) {

    try {
      this.timeoutSeconds = timeoutSeconds;
      this.client = SshClient.setUpDefaultClient();
      client.start();

      session = client.connect(username, host, port)
          .verify(timeoutSeconds, TimeUnit.SECONDS)
          .getSession();

      Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(session, null, new FileInputStream(privateKeyPath), null);
      for (KeyPair kp : keyPairs) {
        session.addPublicKeyIdentity(kp);
      }

      session.auth().verify(timeoutSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public CommandResult execute(String command) throws IOException {
    try (ByteArrayOutputStream stdout = new ByteArrayOutputStream();
         ByteArrayOutputStream stderr = new ByteArrayOutputStream();
         ClientChannel channel = session.createExecChannel(command)) {

      channel.setOut(stdout);
      channel.setErr(stderr);

      channel.open().verify(timeoutSeconds, TimeUnit.SECONDS);
      channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(timeoutSeconds));

      int exitCode = channel.getExitStatus() != null ? channel.getExitStatus() : -1;
      return new CommandResult(exitCode, stdout.toString(), stderr.toString());
    }
  }

  @Override
  public void close() throws IOException {
    session.close(false);
    client.stop();
  }
}