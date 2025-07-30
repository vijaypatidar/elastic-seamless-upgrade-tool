package co.hyperflex.repositories.projection;

import co.hyperflex.entities.precheck.PrecheckSeverity;
import co.hyperflex.entities.precheck.PrecheckStatus;

public record PrecheckStatusAndSeverityView(PrecheckStatus status, PrecheckSeverity severity) {
}
