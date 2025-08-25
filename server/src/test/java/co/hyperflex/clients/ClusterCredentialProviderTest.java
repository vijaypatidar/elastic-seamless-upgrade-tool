package co.hyperflex.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.hyperflex.entities.cluster.ClusterEntity;
import co.hyperflex.entities.cluster.SelfManagedClusterEntity;
import org.junit.jupiter.api.Test;

class ClusterCredentialProviderTest {

  private final ClusterCredentialProvider provider = new ClusterCredentialProvider();

  @Test
  void getAuthHeader_withApiKey() {
    // Arrange
    ClusterEntity cluster = new SelfManagedClusterEntity();
    cluster.setApiKey("my-api-key");

    // Act
    Header header = provider.getAuthHeader(cluster);

    // Assert
    assertEquals("Authorization", header.key());
    assertEquals("ApiKey my-api-key", header.value());
  }

  @Test
  void getAuthHeader_withUsernamePassword() {
    // Arrange
    ClusterEntity cluster = new SelfManagedClusterEntity();
    cluster.setUsername("user");
    cluster.setPassword("pass");

    // Act
    Header header = provider.getAuthHeader(cluster);

    // Assert
    assertEquals("Authorization", header.key());
    assertEquals("Basic dXNlcjpwYXNz", header.value()); // user:pass in base64
  }

  @Test
  void getAuthHeader_noCredentials_throwsException() {
    // Arrange
    ClusterEntity cluster = new SelfManagedClusterEntity();

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      provider.getAuthHeader(cluster);
    });
  }
}
