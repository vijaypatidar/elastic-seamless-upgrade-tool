package co.hyperflex.repositories.projection;

import co.hyperflex.precheck.enums.PrecheckSeverity;
import co.hyperflex.precheck.enums.PrecheckStatus;

public record PrecheckStatusAndSeverityView(PrecheckStatus status, PrecheckSeverity severity) {
}
