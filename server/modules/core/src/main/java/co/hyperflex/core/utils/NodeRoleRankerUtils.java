package co.hyperflex.core.utils;

import java.util.List;

public class NodeRoleRankerUtils {

  public static int getNodeRankByRoles(List<String> nodeRoles, boolean isActiveMaster) {
    final List<String> roles = nodeRoles.stream()
        .map(String::toLowerCase)
        .toList();

    boolean isMaster = roles.contains("master");
    boolean isIngest = roles.contains("ingest");
    boolean isML = roles.contains("ml");
    boolean isTransform = roles.contains("transform");
    boolean isRemote = roles.contains("remote_cluster_client");
    boolean isKibana = roles.contains("kibana");

    boolean isDataFrozen = roles.contains("data_frozen");
    boolean isDataCold = roles.contains("data_cold");
    boolean isDataWarm = roles.contains("data_warm");
    boolean isDataHot = roles.contains("data_hot");
    boolean isDataContent = roles.contains("data_content");
    boolean isGeneralData = roles.contains("data");

    boolean isAnyData = isDataFrozen || isDataCold || isDataWarm || isDataHot || isDataContent || isGeneralData;

    if (isKibana) {
      return 200;
    }
    if (isActiveMaster) {
      return 150;
    }

    int rank = 0;

    if (isAnyData) {
      int dataScore = 7;
      if (isDataHot) {
        dataScore = 6;
      } else if (isDataContent) {
        dataScore = 5;
      } else if (isDataWarm) {
        dataScore = 4;
      } else if (isDataCold) {
        dataScore = 3;
      } else if (isDataFrozen) {
        dataScore = 2;
      }
      rank = 30 + dataScore;
    } else if (isMaster) {
      rank = 80;
    } else if (isML || isTransform || isRemote) {
      rank = 20;
    } else if (isIngest) {
      rank = 15;
    } else {
      rank = 10; // Coordinating-only
    }

    if (isMaster && isAnyData) {
      rank += 15;
    }

    return rank;
  }
}
