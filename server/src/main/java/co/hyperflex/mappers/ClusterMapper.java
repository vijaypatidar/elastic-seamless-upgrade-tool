package co.hyperflex.mappers;

import co.hyperflex.dtos.clusters.AddClusterKibanaNodeRequest;
import co.hyperflex.dtos.clusters.AddClusterRequest;
import co.hyperflex.dtos.clusters.AddElasticCloudClusterRequest;
import co.hyperflex.dtos.clusters.AddSelfManagedClusterRequest;
import co.hyperflex.dtos.clusters.GetClusterKibanaNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterNodeResponse;
import co.hyperflex.dtos.clusters.GetClusterResponse;
import co.hyperflex.dtos.clusters.GetElasticCloudClusterResponse;
import co.hyperflex.dtos.clusters.GetSelfManagedClusterResponse;
import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.ClusterNode;
import co.hyperflex.entities.cluster.ClusterNodeType;
import co.hyperflex.entities.cluster.ElasticCloudCluster;
import co.hyperflex.entities.cluster.KibanaNode;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ClusterMapper {

  public Cluster toEntity(AddClusterRequest request) {
    if (request instanceof AddSelfManagedClusterRequest selfManagedRequest) {
      SelfManagedCluster cluster = new SelfManagedCluster();
      cluster.setName(selfManagedRequest.getName());
      cluster.setElasticUrl(selfManagedRequest.getUrl());
      cluster.setUsername(selfManagedRequest.getUsername());
      cluster.setKibanaUrl(selfManagedRequest.getKibanaUrl());
      cluster.setPassword(selfManagedRequest.getPassword());
      return cluster;
    } else if (request instanceof AddElasticCloudClusterRequest elasticCloudRequest) {
      ElasticCloudCluster cluster = new ElasticCloudCluster();
      cluster.setName(elasticCloudRequest.getName());
      cluster.setApiKey(elasticCloudRequest.getApiKey());
      cluster.setDeploymentId(elasticCloudRequest.getDeploymentId());
      return cluster;
    }
    throw new IllegalArgumentException("Unknown AddClusterRequest type");
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

  public GetClusterResponse toGetClusterResponse(Cluster cluster, List<GetClusterKibanaNodeResponse> kibanaNodes) {
    if (cluster instanceof SelfManagedCluster selfManagedCluster) {
      GetSelfManagedClusterResponse response = new GetSelfManagedClusterResponse();
      response.setId(selfManagedCluster.getId());
      response.setName(selfManagedCluster.getName());
      response.setElasticUrl(selfManagedCluster.getElasticUrl());
      response.setKibanaUrl(selfManagedCluster.getKibanaUrl());
      response.setUsername(selfManagedCluster.getUsername());
      response.setKibanaNodes(kibanaNodes);
      return response;
    } else if (cluster instanceof ElasticCloudCluster elasticCloudCluster) {
      GetElasticCloudClusterResponse response = new GetElasticCloudClusterResponse();
      response.setId(elasticCloudCluster.getId());
      response.setName(elasticCloudCluster.getName());
      response.setDeploymentId(elasticCloudCluster.getDeploymentId());
      response.setApiKey(elasticCloudCluster.getApiKey());
      return response;
    }
    throw new IllegalArgumentException("Unknown Cluster type");
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
