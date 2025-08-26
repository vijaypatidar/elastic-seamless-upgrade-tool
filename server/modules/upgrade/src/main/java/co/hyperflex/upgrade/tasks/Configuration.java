package co.hyperflex.upgrade.tasks;

public record Configuration(int esTransportPort, int esHttpPort, String sshUser, String sshKeyPath,
                            String targetVersion) {
}
