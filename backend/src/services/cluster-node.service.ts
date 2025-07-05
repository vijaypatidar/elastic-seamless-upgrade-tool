import logger from "../logger/logger";
import {
	ClusterNode,
	ClusterNodeType,
	IClusterNode,
	IClusterNodeDocument,
	IElasticNode,
	IKibanaNode,
} from "../models/cluster-node.model";

class ClusterNodeService {
	async getNodes(clusterId: string, type?: ClusterNodeType): Promise<IClusterNodeDocument[]> {
		return await ClusterNode.find({ clusterId: clusterId, type: type });
	}

	async getElasticNodeById(nodeId: string): Promise<IElasticNode | null> {
		const node = await this.getNodeById(nodeId, ClusterNodeType.ELASTIC);
		return (node as IElasticNode) || null;
	}

	async getKibanaNodeById(nodeId: string): Promise<IKibanaNode | null> {
		const node = await this.getNodeById(nodeId, ClusterNodeType.KIBANA);
		return (node as IKibanaNode) || null;
	}

	async updateNodesPartially(identifier: Partial<IClusterNode>, updates: Partial<IClusterNode>) {
		try {
			await ClusterNode.updateMany(identifier, { $set: updates });
		} catch (error) {
			logger.debug(`Error updating nodes with identifier ${JSON.stringify(identifier)}:`, error);
		}
	}

	async createOrUpdateNode(node: IClusterNode): Promise<IClusterNodeDocument> {
		return await ClusterNode.findOneAndUpdate({ nodeId: node.nodeId }, node, {
			new: true,
			upsert: true,
			runValidators: true,
		});
	}

	private async getNodeById(nodeId: string, type: ClusterNodeType): Promise<IClusterNode | null> {
		return await ClusterNode.findOne({ nodeId: nodeId });
	}
}

export const clusterNodeService = new ClusterNodeService();
