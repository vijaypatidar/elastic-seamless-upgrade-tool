package co.hyperflex.ansible;

import java.util.List;

public record AnsibleAdHocCommandResult(
    boolean success,
    List<String> stdOutLogs,
    List<String> strErrLogs
) {
}
