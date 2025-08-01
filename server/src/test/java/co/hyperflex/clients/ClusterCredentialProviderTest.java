package co.hyperflex.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.hyperflex.entities.cluster.Cluster;
import co.hyperflex.entities.cluster.SelfManagedCluster;
import org.apache.http.Header;
import org.junit.jupiter.api.Test;

class ClusterCredentialProviderTest {

  private final ClusterCredentialProvider provider = new ClusterCredentialProvider();

  @Test
  void getAuthHeader_withApiKey() {
    // Arrange
    Cluster cluster = new SelfManagedCluster();
    cluster.setApiKey("my-api-key");

    // Act
    Header header = provider.getAuthHeader(cluster);

    // Assert
    assertEquals("Authorization", header.getName());
    assertEquals("ApiKey my-api-key", header.getValue());
  }

  @Test
  void getAuthHeader_withUsernamePassword() {
    // Arrange
    Cluster cluster = new SelfManagedCluster();
    cluster.setUsername("user");
    cluster.setPassword("pass");

    // Act
    Header header = provider.getAuthHeader(cluster);

    // Assert
    assertEquals("Authorization", header.getName());
    assertEquals("Basic dXNlcjpwYXNz", header.getValue()); // user:pass in base64
  }

  @Test
  void getAuthHeader_noCredentials_throwsException() {
    // Arrange
    Cluster cluster = new SelfManagedCluster();

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      provider.getAuthHeader(cluster);
    });
  }
}
