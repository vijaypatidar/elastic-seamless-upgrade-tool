package co.hyperflex.dtos;

import co.hyperflex.entities.precheck.PrecheckStatus;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ClusterInfoResponse(
    @NotNull Elastic elastic,
    @NotNull Kibana kibana,
    @NotNull Precheck precheck
) {
  public record Elastic(
      boolean isUpgradable,
      Deprecations deprecations,
      SnapshotWrapper snapshot,
      String creationPage
  ) {
    public record SnapshotWrapper(
        Snapshot snapshot,
        String creationPage
    ) {
    }

    public record Snapshot(
        Instant createdAt,
        String name
    ) {
    }
  }

  public record Kibana(
      @NotNull boolean isUpgradable,
      Deprecations deprecations
  ) {
  }

  public record Deprecations(
      int critical,
      int warning
  ) {
  }

  public record Precheck(
      @NotNull PrecheckStatus status
  ) {
  }
}
