import ClusterInfo, {
  IClusterInfo,
  IClusterInfoDocument,
} from '../models/cluster-info.model';

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
    kibana: clusterInfo?.kibana,
  };
};
