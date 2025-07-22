package com.hyperflex.dtos;

import com.hyperflex.entities.cluster.ClusterNodeType;
import com.hyperflex.entities.upgrade.ClusterNodeUpgradeStatus;
import java.util.List;

public record GetClusterNodeResponse(
    String id,
    String name,
    String ip,
    String version,
    List<String> roles,
    ClusterNodeType type,
    String clusterId,
    int progress,
    ClusterNodeUpgradeStatus status,
    String os
) {
}

