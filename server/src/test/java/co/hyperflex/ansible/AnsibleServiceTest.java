package co.hyperflex.ansible;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.hyperflex.ansible.commands.AnsibleAdHocAptCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocShellCommand;
import co.hyperflex.ansible.commands.AnsibleAdHocSystemdCommand;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnsibleServiceTest {

  @Spy
  @InjectMocks
  private AnsibleService ansibleService;

  @Test
  void run_aptCommand_success() throws Exception {
    // Arrange
    AnsibleAdHocAptCommand cmd = new AnsibleAdHocAptCommand.Builder()
        .hostIp("127.0.0.1")
        .args(Map.of("name", "nginx", "state", "present"))
        .sshUsername("user")
        .sshKeyPath("/path/to/key")
        .build();

    Process process = mock(Process.class);
    InputStream inputStream = new ByteArrayInputStream("stdout line".getBytes());
    InputStream errorStream = new ByteArrayInputStream("stderr line".getBytes());

    when(process.getInputStream()).thenReturn(inputStream);
    when(process.getErrorStream()).thenReturn(errorStream);
    when(process.waitFor()).thenReturn(0);
    doReturn(process).when(ansibleService).getProcess(cmd);

    Consumer<String> stdLogsConsumer = mock(Consumer.class);
    Consumer<String> errLogsConsumer = mock(Consumer.class);

    // Act
    int exitCode = ansibleService.run(cmd, stdLogsConsumer, errLogsConsumer);

    // Assert
    assertEquals(0, exitCode);
    verify(stdLogsConsumer).accept("stdout line");
    verify(errLogsConsumer).accept("stderr line");
  }

  @Test
  void run_shellCommand_success() throws Exception {
    // Arrange
    AnsibleAdHocShellCommand cmd = new AnsibleAdHocShellCommand.Builder()
        .hostIp("127.0.0.1")
        .args("echo hello")
        .sshUsername("user")
        .sshKeyPath("/path/to/key")
        .build();

    Process process = mock(Process.class);
    InputStream inputStream = new ByteArrayInputStream("hello".getBytes());
    InputStream errorStream = new ByteArrayInputStream("".getBytes());

    when(process.getInputStream()).thenReturn(inputStream);
    when(process.getErrorStream()).thenReturn(errorStream);
    when(process.waitFor()).thenReturn(0);
    doReturn(process).when(ansibleService).getProcess(cmd);

    Consumer<String> stdLogsConsumer = mock(Consumer.class);
    Consumer<String> errLogsConsumer = mock(Consumer.class);

    // Act
    int exitCode = ansibleService.run(cmd, stdLogsConsumer, errLogsConsumer);

    // Assert
    assertEquals(0, exitCode);
    verify(stdLogsConsumer).accept("hello");
    verify(errLogsConsumer, never()).accept(any());
  }

  @Test
  void run_systemdCommand_success() throws Exception {
    // Arrange
    AnsibleAdHocSystemdCommand cmd = new AnsibleAdHocSystemdCommand.Builder()
        .hostIp("127.0.0.1")
        .args(Map.of("name", "nginx", "state", "restarted"))
        .sshUsername("user")
        .sshKeyPath("/path/to/key")
        .build();

    Process process = mock(Process.class);
    InputStream inputStream = new ByteArrayInputStream("restarted".getBytes());
    InputStream errorStream = new ByteArrayInputStream("".getBytes());

    when(process.getInputStream()).thenReturn(inputStream);
    when(process.getErrorStream()).thenReturn(errorStream);
    when(process.waitFor()).thenReturn(0);
    doReturn(process).when(ansibleService).getProcess(cmd);

    Consumer<String> stdLogsConsumer = mock(Consumer.class);
    Consumer<String> errLogsConsumer = mock(Consumer.class);

    // Act
    int exitCode = ansibleService.run(cmd, stdLogsConsumer, errLogsConsumer);

    // Assert
    assertEquals(0, exitCode);
    verify(stdLogsConsumer).accept("restarted");
    verify(errLogsConsumer, never()).accept(any());
  }
}
