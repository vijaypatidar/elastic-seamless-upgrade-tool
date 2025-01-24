import Migration from '@elastic/elasticsearch/lib/api/api/migration';
import { ElasticClient } from '../clients/elastic.client';
import { DeprecationCounts, DepricationSetting } from '../interfaces';
import ClusterInfo, {
  IClusterInfo,
  IClusterInfoDocument,
} from '../models/cluster-info.model';
import { MigrationDeprecationsDeprecation, MigrationDeprecationsDeprecationLevel } from '@elastic/elasticsearch/lib/api/types';

export const createOrUpdateClusterInfo = async (
  clusterInfo: IClusterInfo,
): Promise<IClusterInfoDocument> => {
  // TODO These needs to be updated when we want to support multiple clusters
  const clusterId = 'cluster-id'; //clusterInfo.clusterId
  const data = await ClusterInfo.findOneAndUpdate(
    { clusterId: clusterId },
    { ...clusterInfo, clusterId: clusterId },
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
  };
};


export const getElasticsearchDeprecation = async(clusterId: string): Promise<[DeprecationCounts, DepricationSetting[]]> =>{
  try {
      const client = await ElasticClient.buildClient(clusterId);
      const data = await client.getClient().migration.deprecations();

    let criticalCount = 0;
    let warningCount = 0;
    let deprications: DepricationSetting[] = [];
    

    if (data.cluster_settings) {
      data.cluster_settings.forEach((item: MigrationDeprecationsDeprecation) => {
        if (item.level === "critical") criticalCount++;
        if (item.level === "warning") warningCount++;
        deprications.push({
          issue: item.message,
          issueDetails: item.details,
          resolution: item.url,
          type: item.level
        })
      });
    }
    if (data.node_settings) {
      data.node_settings.forEach((item: MigrationDeprecationsDeprecation) => {
        if (item.level === "critical") criticalCount++;
        if (item.level === "warning") warningCount++;
        deprications.push({
          issue: item.message,
          issueDetails: item.details,
          resolution: item.url,
          type: item.level
        })
      });
    }

    if (data.index_settings) {
      Object.values(data.index_settings).forEach((indexArray: any[]) => {
        indexArray.forEach((item: MigrationDeprecationsDeprecation) => {
          if (item.level === "critical") criticalCount++;
          if (item.level === "warning") warningCount++;
          deprications.push({
            issue: item.message,
            issueDetails: item.details,
            resolution: item.url,
            type: item.level
          })
        });
      });
    }
    return [{ critical: criticalCount, warning: warningCount },deprications];
  } catch (error) {
    console.error("Error fetching Elasticsearch deprecations:", error);
    throw error;
  }
}

//upgrade after adding kibana client
export const getKibanaDeprication = async(kibanaUrl: string)=>{
  const res = await fetch(`${kibanaUrl}/api/deprecations/`);
  const data = await res.json();
  console.log(data);
  return [data,0]
}