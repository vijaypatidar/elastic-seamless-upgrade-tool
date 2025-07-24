package co.hyperflex.mappers;

import co.hyperflex.dtos.AddClusterKibanaNodeRequest;
import co.hyperflex.dtos.AddClusterRequest;
import co.hyperflex.dtos.GetClusterNodeResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.cluster.KibanaNode;
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
    ClusterNode node = new KibanaNode();
    node.setId(UUID.randomUUID().toString());
    node.setName(request.name());
    node.setIp(request.ip());
    node.setVersion("7.17.0");
    node.setRoles(List.of("kibana"));
    node.setType(ClusterNodeType.KIBANA);
    return node;
  }

  public GetClusterNodeResponse toGetClusterNodeResponse(ClusterNode node) {
    return new GetClusterNodeResponse(
        node.getId(),
        node.getName(),
        node.getIp(),
        node.getVersion(),
        node.getRoles(),
        node.getType(),
        node.getClusterId(),
        node.getProgress(),
        null,
        null
    );
  }

}
