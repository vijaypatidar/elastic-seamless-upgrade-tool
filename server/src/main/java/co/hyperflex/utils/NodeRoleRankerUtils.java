package co.hyperflex.utils;

import java.util.List;

public class NodeRoleRankerUtils {

  public static int getNodeRankByRoles(List<String> nodeRoles, boolean isActiveMaster) {
    final List<String>  roles = nodeRoles.stream().map(String::toLowerCase).toList();
    boolean isMaster = roles.contains("master");
    boolean isData = roles.stream().anyMatch(role -> role.startsWith("data"));
    boolean isIngest = roles.contains("ingest");
    boolean isML = roles.contains("ml");
    boolean isTransform = roles.contains("transform");
    boolean isRemote = roles.contains("remote_cluster_client");
    boolean isKibana = roles.contains("kibana");

    // Coordinating-only: no special roles
    boolean isCoordinatingOnly =
        !isMaster && !isData && !isIngest && !isML && !isTransform && !isRemote;

    if (isKibana) {
      return 100; // Kibana must be upgraded after any elastic node
    } else if (isActiveMaster) {
      return 60; // Active master node (upgrade last)
    } else if (isCoordinatingOnly) {
      return 10;           // coordinating-only node
    } else if (isIngest) {
      return 15;        // ingest-only or ingest node
    } else if (isML || isRemote || isTransform) {
      return 20; // ml, transform, remote_cluster_client
    } else if (isData && !isMaster) {
      return 30;          // data-only node
    } else if (isData) {
      return 40;           // data + master node
    } else if (isMaster) {
      return 50;          // master-only node
    } else {
      return 100; // Fallback for unknown role combinations
    }
  }
}
