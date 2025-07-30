package co.hyperflex.utils;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NodeRoleRankerUtilsTest {

  @Test
  void testMasterEligibleAndDataRoleRank() {
    int rank = NodeRoleRankerUtils.getNodeRankByRoles(List.of("data", "master"), false);
    Assertions.assertEquals(40, rank);
  }

  @Test
  void testMasterEligibleRoleRank() {
    int rank = NodeRoleRankerUtils.getNodeRankByRoles(List.of("master"), false);
    Assertions.assertEquals(50, rank);
  }
}