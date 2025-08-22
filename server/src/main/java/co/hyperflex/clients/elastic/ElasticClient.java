package co.hyperflex.clients.elastic;

import co.hyperflex.clients.elastic.dto.GetElasticDeprecationResponse;
import co.hyperflex.clients.elastic.dto.cat.health.HealthRecord;
import co.hyperflex.clients.elastic.dto.cat.indices.FlushResponse;
import co.hyperflex.clients.elastic.dto.cat.indices.IndicesRecord;
import co.hyperflex.clients.elastic.dto.cat.master.MasterRecord;
import co.hyperflex.clients.elastic.dto.cat.shards.ShardsRecord;
import co.hyperflex.clients.elastic.dto.cluster.AllocationExplainRequest;
import co.hyperflex.clients.elastic.dto.cluster.AllocationExplainResponse;
import co.hyperflex.clients.elastic.dto.cluster.ClusterStatsResponse;
import co.hyperflex.clients.elastic.dto.cluster.GetClusterSettingsResponse;
import co.hyperflex.clients.elastic.dto.cluster.PutClusterSettingsResponse;
import co.hyperflex.clients.elastic.dto.cluster.health.ClusterHealthResponse;
import co.hyperflex.clients.elastic.dto.nodes.NodesInfoResponse;
import co.hyperflex.clients.elastic.dto.nodes.NodesStatsResponse;
import co.hyperflex.dtos.GetElasticNodeAndIndexCountsResponse;
import co.hyperflex.dtos.GetElasticsearchSnapshotResponse;
import co.hyperflex.dtos.recovery.GetAllocationExplanationResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestClient;

public interface ElasticClient {

  RestClient getRestClient();

  List<HealthRecord> getHealth();

  List<IndicesRecord> getIndices();

  String getHealthStatus();

  List<MasterRecord> getActiveMasters();

  Boolean isAdaptiveReplicaEnabled() throws IOException;

  List<GetElasticsearchSnapshotResponse> getValidSnapshots();

  GetElasticNodeAndIndexCountsResponse getEntitiesCounts();

  GetElasticDeprecationResponse getDeprecation();

  List<GetAllocationExplanationResponse> getAllocationExplanation();

  AllocationExplainResponse getAllocationExplanation(AllocationExplainRequest request);

  GetClusterSettingsResponse getClusterSettings();

  PutClusterSettingsResponse updateClusterSettings(Map<String, Object> clusterSettings);

  List<ShardsRecord> getShards();

  List<ShardsRecord> getShards(String indexName);

  FlushResponse flushIndices();

  ClusterHealthResponse getClusterHealth();

  co.hyperflex.clients.elastic.dto.info.InfoResponse getInfo();

  NodesInfoResponse getNodesInfo();

  NodesInfoResponse getNodeInfo(String nodeId);

  NodesStatsResponse getNodesStats(String nodeId);

  ClusterStatsResponse getClusterStats();
}
