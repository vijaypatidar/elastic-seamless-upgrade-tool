import ClusterInfo, { IClusterInfo } from '../models/cluster-info.model';

export const createOrUpdateClusterInfo = async (
  clusterInfo: Partial<IClusterInfo>,
): Promise<Partial<IClusterInfo>> => {
  const data = await ClusterInfo.findOneAndUpdate(
    { clusterId: clusterInfo.clusterId },
    clusterInfo, // Data to update or create
    { new: true, upsert: true, runValidators: true },
  );
  return clusterInfo;
};

export const getClusterInfoById = async (
  clusterId: string = 'cluster-id',
): Promise<Partial<IClusterInfo>> => {
  const clusterInfo = await ClusterInfo.findOne({
    clusterId: clusterId,
  });
  return {
    clusterId,
    elastic: clusterInfo?.elastic,
  };
};
