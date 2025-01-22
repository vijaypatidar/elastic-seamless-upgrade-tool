import ClusterInfo, {
  IClusterInfo,
  IClusterInfoDocument,
} from '../models/cluster-info.model';

export const createOrUpdateClusterInfo = async (
  clusterInfo: IClusterInfo,
): Promise<IClusterInfoDocument> => {
  const data = await ClusterInfo.findOneAndUpdate(
    { clusterId: clusterInfo.clusterId },
    clusterInfo, // Data to update or create
    { new: true, upsert: true, runValidators: true },
  );
  return data;
};

export const getClusterInfoById = async (
  clusterId: string = 'cluster-id',
): Promise<IClusterInfo> => {
  const clusterInfo = await ClusterInfo.findOne({
    clusterId: clusterId,
  });
  return {
    clusterId,
    elastic: clusterInfo?.elastic!!,
  };
};
