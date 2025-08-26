package co.hyperflex.prechecks.repositories.projection;

import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.precheck.enums.PrecheckStatus;

public record PrecheckStatusAndSeverityView(PrecheckStatus status, PrecheckSeverity severity) {
}
