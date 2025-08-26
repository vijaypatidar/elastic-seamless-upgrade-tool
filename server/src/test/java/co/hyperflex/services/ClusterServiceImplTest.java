package co.hyperflex.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.hyperflex.clients.ClusterCredentialProvider;
import co.hyperflex.clients.elastic.ElasticClient;
import co.hyperflex.clients.elastic.ElasticClientImpl;
import co.hyperflex.clients.elastic.ElasticsearchClientProvider;
import co.hyperflex.clients.elastic.dto.nodes.NodesInfoResponse;
import co.hyperflex.clients.kibana.KibanaClient;
import co.hyperflex.clients.kibana.KibanaClientImpl;
import co.hyperflex.clients.kibana.KibanaClientProvider;
import co.hyperflex.common.client.ClientConnectionDetail;
import co.hyperflex.common.exceptions.BadRequestException;
import co.hyperflex.common.exceptions.NotFoundException;
import co.hyperflex.core.entites.clusters.SelfManagedClusterEntity;
import co.hyperflex.core.mappers.ClusterMapper;
import co.hyperflex.core.repositories.ClusterNodeRepository;
import co.hyperflex.core.repositories.ClusterRepository;
import co.hyperflex.core.services.clusters.dtos.AddClusterResponse;
import co.hyperflex.core.services.clusters.dtos.AddSelfManagedClusterRequest;
import co.hyperflex.core.services.clusters.dtos.GetClusterResponse;
import co.hyperflex.core.services.clusters.dtos.UpdateClusterResponse;
import co.hyperflex.core.services.clusters.dtos.UpdateSelfManagedClusterRequest;
import co.hyperflex.core.services.ssh.SshKeyService;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClusterServiceImplTest {

  public static final String MOCK_ELASTIC_SEARCH_URL = "http://localhost.com:9200";
  public static final String MOCK_KIBANA_URL = "http://localhost.com:5601";
  @Mock
  private ClusterRepository clusterRepository;
  @Mock
  private ClusterNodeRepository clusterNodeRepository;
  @Mock
  private ClusterMapper clusterMapper;
  @Mock
  private ElasticsearchClientProvider elasticsearchClientProvider;
  @Mock
  private KibanaClientProvider kibanaClientProvider;
  @Mock
  private SshKeyService sshKeyService;
  @Mock
  private ClusterCredentialProvider clusterCredentialProvider;

  @InjectMocks
  private ClusterServiceImpl clusterService;


  @Test
  void add_selfManagedCluster_success() throws IOException {
    // Arrange
    AddSelfManagedClusterRequest request = new AddSelfManagedClusterRequest();
    request.setName("test-cluster");
    SelfManagedClusterEntity cluster = new SelfManagedClusterEntity();

    cluster.setName("test-cluster");
    cluster.setElasticUrl(MOCK_ELASTIC_SEARCH_URL);
    cluster.setKibanaUrl(MOCK_KIBANA_URL);

    co.hyperflex.clients.elastic.ElasticClient elasticClient = mock(ElasticClientImpl.class);
    KibanaClient kibanaClient = mock(KibanaClientImpl.class);
    NodesInfoResponse nodesInfoResponse = mock(NodesInfoResponse.class);

    when(clusterMapper.toEntity(request)).thenReturn(cluster);
    when(elasticsearchClientProvider.getClient(any(ClientConnectionDetail.class))).thenReturn(elasticClient);
    when(kibanaClientProvider.getClient(any(ClientConnectionDetail.class))).thenReturn(kibanaClient);
    when(elasticClient.getHealthStatus()).thenReturn("green");
    when(kibanaClient.getKibanaVersion()).thenReturn("8.1.0");

    when(elasticClient.getNodesInfo()).thenReturn(nodesInfoResponse);
    when(nodesInfoResponse.getNodes()).thenReturn(Collections.emptyMap());
    when(elasticClient.getActiveMasters()).thenReturn(Collections.emptyList());

    // Act
    AddClusterResponse response = clusterService.add(request);

    // Assert
    assertNotNull(response);
    verify(clusterRepository).save(cluster);
  }

  @Test
  void updateCluster_selfManagedCluster_success() {
    // Arrange
    UpdateSelfManagedClusterRequest request = new UpdateSelfManagedClusterRequest();
    request.setName("updated-cluster");
    request.setElasticUrl(MOCK_ELASTIC_SEARCH_URL);
    request.setKibanaUrl(MOCK_KIBANA_URL);
    request.setSshKey("new-key");
    request.setSshUsername("new-user");

    String clusterId = "cluster-id";
    SelfManagedClusterEntity cluster = new SelfManagedClusterEntity();
    cluster.setId(clusterId);

    KibanaClient kibanaClient = mock(KibanaClientImpl.class);
    ElasticClient esClient = mock(ElasticClient.class);
    NodesInfoResponse nodesInfoResponse = mock(NodesInfoResponse.class);

    when(clusterRepository.findById(clusterId)).thenReturn(Optional.of(cluster));
    when(sshKeyService.createSSHPrivateKeyFile(any(), any())).thenReturn("path/to/key");
    var esConnectionDetail = new ClientConnectionDetail(MOCK_ELASTIC_SEARCH_URL, null);
    var kbConnectionDetail = new ClientConnectionDetail(MOCK_KIBANA_URL, null);

    when(elasticsearchClientProvider.getClient(esConnectionDetail)).thenReturn(esClient);
    when(kibanaClientProvider.getClient(kbConnectionDetail)).thenReturn(kibanaClient);
    when(esClient.getNodesInfo()).thenReturn(nodesInfoResponse);
    when(esClient.getHealthStatus()).thenReturn("green");
    when(kibanaClient.getKibanaVersion()).thenReturn("8.1.0");
    when(esClient.getNodesInfo()).thenReturn(nodesInfoResponse);
    when(nodesInfoResponse.getNodes()).thenReturn(Collections.emptyMap());
    when(esClient.getActiveMasters()).thenReturn(Collections.emptyList());

    // Act
    UpdateClusterResponse response = clusterService.updateCluster(clusterId, request);

    // Assert
    assertNotNull(response);
    verify(clusterRepository).save(cluster);
    assertEquals("updated-cluster", cluster.getName());
  }

  @Test
  void getClusterById_found() {
    // Arrange
    String clusterId = "cluster-id";
    SelfManagedClusterEntity cluster = new SelfManagedClusterEntity();
    cluster.setId(clusterId);
    GetClusterResponse getClusterResponse = mock(GetClusterResponse.class);

    when(clusterRepository.findById(clusterId)).thenReturn(Optional.of(cluster));
    when(clusterNodeRepository.findByClusterId(clusterId)).thenReturn(Collections.emptyList());
    when(clusterMapper.toGetClusterResponse(cluster, Collections.emptyList())).thenReturn(getClusterResponse);

    // Act
    GetClusterResponse response = clusterService.getClusterById(clusterId);

    // Assert
    assertNotNull(response);
    verify(clusterRepository).findById(clusterId);
  }

  @Test
  void getClusterById_notFound() {
    // Arrange
    String clusterId = "cluster-id";
    when(clusterRepository.findById(clusterId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> clusterService.getClusterById(clusterId));
  }

  @Test
  void add_invalidCredentials_throwsBadRequest() {
    // Arrange
    AddSelfManagedClusterRequest request = new AddSelfManagedClusterRequest();
    SelfManagedClusterEntity cluster = new SelfManagedClusterEntity();
    cluster.setElasticUrl(MOCK_ELASTIC_SEARCH_URL);
    cluster.setKibanaUrl(MOCK_KIBANA_URL);
    var connectionDetail = new ClientConnectionDetail(MOCK_ELASTIC_SEARCH_URL, null);
    when(clusterMapper.toEntity(request)).thenReturn(cluster);
    when(elasticsearchClientProvider.getClient(connectionDetail)).thenThrow(new RuntimeException("Invalid credentials"));

    // Act & Assert
    assertThrows(BadRequestException.class, () -> clusterService.add(request));

    verify(clusterMapper).toEntity(request);
  }
}
