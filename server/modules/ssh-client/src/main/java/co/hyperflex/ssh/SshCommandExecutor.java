package co.hyperflex.ssh;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshCommandExecutor implements AutoCloseable {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final SshClient client;
  private final ClientSession session;
  private final long timeoutSeconds = 15;

  public SshCommandExecutor(String host, int port, String username, String privateKeyPath) {

    try {
      this.client = SshClient.setUpDefaultClient();
      client.start();

      session = client.connect(username, host, port)
          .verify(timeoutSeconds, TimeUnit.SECONDS)
          .getSession();

      try (var stream = new FileInputStream(privateKeyPath)) {
        Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(session, null, stream, null);
        for (KeyPair kp : keyPairs) {
          session.addPublicKeyIdentity(kp);
        }
      }
      session.auth().verify(timeoutSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      if (e.getCause() instanceof TimeoutException) {
        logger.warn("Timeout waiting for private key verification");
        throw new RuntimeException("Unable to establish SSH connection to host (IP: " + host + ").");
      }
      logger.warn("Unable to establish SSH connection to host", e);
      throw new RuntimeException("SSH authentication failed for host (IP: " + host + ").");
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
      return new CommandResult(exitCode, stdout.toString().trim(), stderr.toString().trim());
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (session != null && session.isOpen()) {
        session.close(false).await();
      }
    } finally {
      if (client != null && client.isOpen()) {
        client.close();
      }
    }
  }

}