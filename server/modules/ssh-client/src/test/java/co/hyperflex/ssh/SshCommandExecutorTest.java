package co.hyperflex.ssh;


import co.hyperflex.ssh.utils.KeyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SshCommandExecutorTest {

  private static SshServer sshServer;
  private static int port;
  private static Path tempKeyFile;

  @BeforeAll
  static void setupServer() throws Exception {
    // Generate a host key
    sshServer = SshServer.setUpDefaultServer();
    sshServer.setPort(0); // 0 = random available port
    sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

    // Accept any public key for simplicity
    sshServer.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);

    sshServer.setCommandFactory((channel, command) -> new Command() {
      @Override
      public void setExitCallback(ExitCallback callback) {
        callback.onExit(0);
      }

      @Override
      public void setErrorStream(OutputStream err) {

      }

      @Override
      public void setInputStream(InputStream in) {

      }

      @Override
      public void setOutputStream(OutputStream out) {
        try {
          out.write("executed: ls -l".getBytes());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void start(ChannelSession channel, Environment env) throws IOException {

      }

      @Override
      public void destroy(ChannelSession channel) throws Exception {

      }
    });

    sshServer.start();
    port = sshServer.getPort();

    KeyPair keyPair = KeyUtils.generateRsaKeyPair();
    tempKeyFile = KeyUtils.writePrivateKeyToPem(keyPair, "id_rsa");
  }

  @AfterAll
  static void tearDownServer() throws Exception {
    if (sshServer != null) {
      sshServer.stop();
    }
    Files.deleteIfExists(tempKeyFile);
  }

  @Test
  void testExecuteCommand() throws Exception {
    try (var executor = new SshCommandExecutor("localhost", port, "testuser", tempKeyFile.toAbsolutePath().toString())) {
      CommandResult result = executor.execute("ls -l");

      Assertions.assertEquals(0, result.exitCode());
      Assertions.assertEquals("executed: ls -l", result.stdout());
      Assertions.assertTrue(result.stderr().isEmpty());
    }
  }
}
