import { ElasticClient } from "../clients/elastic.client";
import logger from "../logger/logger";
import { ClusterNode, IElasticNode, IClusterNodeDocument, ClusterNodeType } from "../models/cluster-node.model";
import { NodeStatus } from "../enums";

export const createOrUpdateElasticNode = async (elasticNode: IElasticNode): Promise<IClusterNodeDocument> => {
	const nodeId = elasticNode.nodeId;
	const data = await ClusterNode.findOneAndUpdate(
		{ nodeId: nodeId, type: ClusterNodeType.ELASTIC },
		{ ...elasticNode },
		{ new: true, upsert: true, runValidators: true }
	);
	return data;
};

export const getElasticNodeById = async (nodeId: string): Promise<IElasticNode | null> => {
	const elasticNode = await ClusterNode.findOne({ nodeId: nodeId, type: ClusterNodeType.ELASTIC });
	if (!elasticNode || elasticNode.type === ClusterNodeType.KIBANA) return null;
	return elasticNode;
};

export const getAllElasticNodes = async (clusterId: string): Promise<IElasticNode[]> => {
	try {
		await syncNodeData(clusterId);
	} catch (error) {
		logger.error("Unable to sync with Elastic search instance! Maybe the connection is breaked");
	} finally {
		const elasticNodes = await ClusterNode.find({ clusterId: clusterId, type: ClusterNodeType.ELASTIC });
		return elasticNodes as IElasticNode[];
	}
};

export const syncNodeData = async (clusterId: string) => {
	try {
		const client = await ElasticClient.buildClient(clusterId);
		const response: any = await client.getClient().nodes.info({
			filter_path: "nodes.*.name,nodes.*.roles,nodes.*.os.name,nodes.*.os.version,nodes.*.version,nodes.*.ip",
		});
		const masterNodes = await client.getMasterNodes();
		const elasticNodes: IElasticNode[] | null = Object.entries(response.nodes).map(
			([key, value]: [string, any]) => ({
				nodeId: key,
				clusterId: clusterId,
				ip: value.ip,
				name: value.name,
				version: value.version,
				roles: value.roles,
				os: value.os,
				progress: 0,
				isMaster: masterNodes.some((master) => master.id === key),
				status: NodeStatus.AVAILABLE,
				type: ClusterNodeType.ELASTIC,
			})
		);
		for (const node of elasticNodes) {
			const existingNode = await ClusterNode.findOne({ nodeId: node.nodeId, type: ClusterNodeType.ELASTIC });
			if (existingNode) {
				node.status = existingNode.status;
				node.progress = existingNode.progress;
			}
			await ClusterNode.findOneAndUpdate({ nodeId: node.nodeId, type: ClusterNodeType.ELASTIC }, node, {
				new: true,
				runValidators: true,
				upsert: true,
			});
		}
	} catch (error) {
		logger.error("Error syncing nodes from Elasticsearch:", error);
	}
};

export const updateNodeStatus = async (
	identifier: Record<string, any>,
	newStatus: string
): Promise<IClusterNodeDocument | null> => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			{ ...identifier, type: ClusterNodeType.ELASTIC },
			{ status: newStatus },
			{ new: true, runValidators: true }
		);

		if (!updatedNode) {
			logger.debug(`Node with identifier ${identifier} not found.`);
			return null;
		}

		return updatedNode;
	} catch (error: any) {
		console.error(`Error updating status for node ${identifier}: ${error.message}`);
		throw error;
	}
};

export const updateNode = async (identifier: Record<string, any>, updatedNodeValues: Partial<IElasticNode>) => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			{ ...identifier, type: ClusterNodeType.ELASTIC },
			{ $set: updatedNodeValues },
			{ new: true }
		);
		if (!updatedNode) {
			throw new Error(`Node with identfier ${identifier} not found`);
		}
	} catch (error) {
		throw new Error(`Error updating node: ${error}`);
	}
};

export const updateNodeProgress = async (identifier: Record<string, any>, progress: number) => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			identifier,
			{ progress: progress },
			{ new: true, runValidators: true }
		);

		if (!updatedNode) {
			logger.debug(`Node with identifier ${identifier} not found.`);
			return null;
		}
		return updatedNode;
	} catch (error: any) {
		console.error(`Error updating progress for node ${identifier}: ${error.message}`);
		throw error;
	}
};
