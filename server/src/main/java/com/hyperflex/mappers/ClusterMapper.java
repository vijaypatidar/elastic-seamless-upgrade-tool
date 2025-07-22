package com.hyperflex.mappers;

import com.hyperflex.dtos.AddClusterKibanaNodeRequest;
import com.hyperflex.dtos.AddClusterRequest;
import com.hyperflex.entities.cluster.Cluster;
import com.hyperflex.entities.cluster.ClusterNode;
import com.hyperflex.entities.cluster.ClusterNodeType;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ClusterMapper {

  public Cluster toEntity(AddClusterRequest request) {
    Cluster cluster = new Cluster();
    cluster.setName(request.name());
    cluster.setUrl(request.url());
    cluster.setUsername(request.username());
    cluster.setKibanaUrl(request.kibanaUrl());
    cluster.setPassword(request.password());
    return cluster;
  }

  public ClusterNode toNodeEntity(AddClusterKibanaNodeRequest request) {
    ClusterNode node = new ClusterNode();
    node.setId(UUID.randomUUID().toString());
    node.setName(request.name());
    node.setIp(request.ip());
    node.setOs("Linux");
    node.setVersion("7.17.0");
    node.setRoles(List.of("kibana"));
    node.setType(ClusterNodeType.KIBANA);
    return node;
  }
}
