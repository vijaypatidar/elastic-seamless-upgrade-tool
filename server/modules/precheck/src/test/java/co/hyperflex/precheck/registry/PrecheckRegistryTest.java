package co.hyperflex.precheck.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.hyperflex.precheck.core.BaseClusterPrecheck;
import co.hyperflex.precheck.core.Precheck;
import co.hyperflex.precheck.core.enums.PrecheckType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrecheckRegistryTest {

  private PrecheckRegistry registry;
  private Precheck<?> clusterPrecheck;

  @BeforeEach
  void setUp() {
    registry = new PrecheckRegistry();
    clusterPrecheck = new BaseClusterPrecheck() {
      @Override
      public String getName() {
        return "Test Cluster Precheck";
      }

      @Override
      public void run(co.hyperflex.precheck.contexts.ClusterContext context) {
      }

      @Override
      public String getId() {
        return "test-cluster-precheck";
      }
    };
  }

  @Test
  void register_shouldStorePrecheck() {
    registry.register(clusterPrecheck);
    assertTrue(registry.getById("test-cluster-precheck").isPresent());
    assertEquals(clusterPrecheck, registry.getById("test-cluster-precheck").get());
  }

  @Test
  void getById_whenPrecheckNotRegistered_shouldReturnEmpty() {
    assertFalse(registry.getById("non-existent").isPresent());
  }

  @Test
  void getAll_shouldReturnAllRegisteredPrechecks() {
    registry.register(clusterPrecheck);
    assertEquals(1, registry.getAll().size());
    assertTrue(registry.getAll().contains(clusterPrecheck));
  }

  @Test
  void getByType_shouldReturnPrechecksOfGivenType() {
    registry.register(clusterPrecheck);
    assertEquals(1, registry.getByType(PrecheckType.CLUSTER).size());
    assertTrue(registry.getByType(PrecheckType.CLUSTER).contains(clusterPrecheck));
    assertTrue(registry.getByType(PrecheckType.NODE).isEmpty());
  }
}
