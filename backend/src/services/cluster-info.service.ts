import Migration from '@elastic/elasticsearch/lib/api/api/migration';
import { ElasticClient } from '../clients/elastic.client';
import { DeprecationCounts, DeprecationSetting } from '../interfaces';
import ClusterInfo, {
  IClusterInfo,
  IClusterInfoDocument,
} from '../models/cluster-info.model';
import {
  MigrationDeprecationsDeprecation,
  MigrationDeprecationsDeprecationLevel,
} from '@elastic/elasticsearch/lib/api/types';
import { DeprecationDetail, KibanaClient } from '../clients/kibana.client';

export const createOrUpdateClusterInfo = async (
  clusterInfo: IClusterInfo,
): Promise<IClusterInfoDocument> => {
  // TODO These needs to be updated when we want to support multiple clusters
  const clusterId = 'cluster-id'; //clusterInfo.clusterId
  const { elastic, kibana, certificateIds } = clusterInfo;
  const data = await ClusterInfo.findOneAndUpdate(
    { clusterId: clusterId },
    {
      elastic: elastic,
      kibana: kibana,
      certificateIds: certificateIds,
      clusterId: clusterId,
    },
    { new: true, upsert: true, runValidators: true },
  );
  return data;
};

export const getClusterInfoById = async (
  clusterId: string,
): Promise<IClusterInfo> => {
  // TODO These needs to be updated when we want to support multiple clusters
  clusterId = 'cluster-id';
  const clusterInfo = await ClusterInfo.findOne({
    clusterId: clusterId,
  });
  return {
    clusterId,
    elastic: clusterInfo?.elastic!!,
    kibana: clusterInfo?.kibana,
  };
};

export const getElasticsearchDeprecation = async (
  clusterId: string,
): Promise<{
  counts: DeprecationCounts;
  deprecations: DeprecationSetting[];
}> => {
  try {
    const client = await ElasticClient.buildClient(clusterId);
    const data = await client.getClient().migration.deprecations();

    let criticalCount = 0;
    let warningCount = 0;
    let deprecations: DeprecationSetting[] = [];

    if (data.cluster_settings) {
      data.cluster_settings.forEach(
        (item: MigrationDeprecationsDeprecation) => {
          if (item.level === 'critical') criticalCount++;
          if (item.level === 'warning') warningCount++;
          deprecations.push({
            issue: item.message,
            issueDetails: item.details,
            resolution: item.url,
            type: item.level,
          });
        },
      );
    }
    if (data.node_settings) {
      data.node_settings.forEach((item: MigrationDeprecationsDeprecation) => {
        if (item.level === 'critical') criticalCount++;
        if (item.level === 'warning') warningCount++;
        deprecations.push({
          issue: item.message,
          issueDetails: item.details,
          resolution: item.url,
          type: item.level,
        });
      });
    }

    if (data.index_settings) {
      Object.values(data.index_settings).forEach((indexArray: any[]) => {
        indexArray.forEach((item: MigrationDeprecationsDeprecation) => {
          if (item.level === 'critical') criticalCount++;
          if (item.level === 'warning') warningCount++;
          deprecations.push({
            issue: item.message,
            issueDetails: item.details,
            resolution: item.url,
            type: item.level,
          });
        });
      });
    }
    return {
      counts: { critical: criticalCount, warning: warningCount },
      deprecations: deprecations,
    };
  } catch (error) {
    console.error('Error fetching Elasticsearch deprecations:', error);
    throw error;
  }
};

//upgrade after adding kibana client
export const getKibanaDeprecation = async (
  clusterId: string,
): Promise<{
  counts: DeprecationCounts;
  deprecations: DeprecationSetting[];
}> => {
  try {
    const client = await KibanaClient.buildClient(clusterId);
    const data: DeprecationDetail[] = await client.getDeprecations();

    let criticalCount = 0;
    let warningCount = 0;
    const deprecations: DeprecationSetting[] = data.map(
      (item: DeprecationDetail) => {
        if (item.level === 'critical') criticalCount++;
        if (item.level === 'warning') warningCount++;

        return {
          issue: item.title,
          issueDetails: item.message,
          type: item.level,
          resolution: item.correctiveActions.manualSteps,
        };
      },
    );
    return {
      counts: { critical: criticalCount, warning: warningCount },
      deprecations: deprecations,
    };
  } catch (error) {
    console.error('Error fetching kibana deprecations:', error);
    throw error;
  }
};
