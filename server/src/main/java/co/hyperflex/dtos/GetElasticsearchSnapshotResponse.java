package co.hyperflex.dtos;

import java.util.Date;

public record GetElasticsearchSnapshotResponse(
    String name,
    Date createdAt
) {
}
