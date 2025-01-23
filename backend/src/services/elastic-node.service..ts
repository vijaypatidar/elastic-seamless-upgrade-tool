import { ElasticClient } from '../clients/elastic.client';
import logger from '../logger/logger';
import ElasticNode, {
  IElasticNode,
  IElasticNodeDocument,
} from '../models/elastic-node.model';

export const createOrUpdateElasticNode = async (
  elasticNode: IElasticNode,
): Promise<IElasticNodeDocument> => {
  const nodeId = elasticNode.nodeId;
  const data = await ElasticNode.findOneAndUpdate(
    { nodeId: nodeId },
    { ...elasticNode },
    { new: true, upsert: true, runValidators: true }, 
  );
  return data;
};

export const getElasticNodeById = async (
  nodeId: string,
): Promise<IElasticNode | null> => {
  const elasticNode = await ElasticNode.findOne({ id: nodeId });
  if (!elasticNode) return null;
  return elasticNode;
};

export const getAllElasticNodes = async (clusterId: string): Promise<IElasticNode[]> => {
  const elasticNodes = await ElasticNode.find({clusterId: clusterId});
  return elasticNodes
};

export const syncNodeData = async (clusterId: string) => {
  try {
    const client = await ElasticClient.buildClient(clusterId);
    const response: any = await client.getClient().nodes.info({
      filter_path:
        'nodes.*.name,nodes.*.roles,nodes.*.os.name,nodes.*.os.version,nodes.*.version,nodes.*.ip',
    });
    const masterNode: any = await client.getClient().cat.master({
      format: 'json',
    });
    console.log('masterNode', masterNode);
    const elasticNodes: IElasticNode[] | null = Object.entries(
      response.nodes,
    ).map(([key, value]: [string, any]) => ({
      nodeId: key,
      clusterId: clusterId,
      ip: value.ip,
      name: value.name,
      version: value.version,
      roles: value.roles,
      os: value.os,
      isMaster: masterNode[0].id === key,
      status: 'available',
    }));

    for (const node of elasticNodes) {
      const existingNode = await ElasticNode.findOne({ id: node.nodeId });
      if (!existingNode) {
        await ElasticNode.create(node);
      } 
    }
  } catch (error) {
    logger.error('Error syncing nodes from Elasticsearch:', error);
    throw error;
  }
};

export const updateNodeStatus = async (
  nodeId: string,
  newStatus: string,
): Promise<IElasticNodeDocument | null> => {
  try {
    const updatedNode = await ElasticNode.findOneAndUpdate(
      { nodeId },
      { status: newStatus },
      { new: true, runValidators: true },
    );

    if (!updatedNode) {
      logger.debug(`Node with id ${nodeId} not found.`);
      return null;
    }

    return updatedNode;
  } catch (error: any) {
    console.error(`Error updating status for node ${nodeId}: ${error.message}`);
    throw error;
  }
};