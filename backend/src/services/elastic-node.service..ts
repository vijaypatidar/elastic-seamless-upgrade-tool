import { error } from "console";
import { ElasticClient } from "../clients/elastic.client";
import { createAnsibleInventory, runPlaybookWithLogging } from "../controllers/ansible-controller";
import { getNodeInfo } from "../controllers/elastic-controller";
import logger from "../logger/logger";
import ElasticNode, { IElasticNode, IElasticNodeDocument } from "../models/elastic-node.model";
import { getClusterInfoById } from "./cluster-info.service";

export const createOrUpdateElasticNode = async (elasticNode: IElasticNode): Promise<IElasticNodeDocument> => {
	const nodeId = elasticNode.nodeId;
	const data = await ElasticNode.findOneAndUpdate(
		{ nodeId: nodeId },
		{ ...elasticNode },
		{ new: true, upsert: true, runValidators: true }
	);
	return data;
};

export const getElasticNodeById = async (nodeId: string): Promise<IElasticNode | null> => {
	const elasticNode = await ElasticNode.findOne({ nodeId: nodeId });
	if (!elasticNode) return null;
	return elasticNode;
};

export const getAllElasticNodes = async (clusterId: string): Promise<IElasticNode[]> => {
	try {
		await syncNodeData(clusterId);
	} catch (error) {
		logger.error("Unable to sync with Elastic search instance! Maybe the connection is breaked");
	} finally {
		const elasticNodes = await ElasticNode.find({ clusterId: clusterId });
		return elasticNodes;
	}
};

export const syncNodeData = async (clusterId: string) => {
	try {
		const client = await ElasticClient.buildClient(clusterId);
		const clusterInfo = await getClusterInfoById(clusterId);
		const response: any = await client.getClient().nodes.info({
			filter_path: "nodes.*.name,nodes.*.roles,nodes.*.os.name,nodes.*.os.version,nodes.*.version,nodes.*.ip",
		});
		const masterNode: any = await client.getClient().cat.master({
			format: "json",
		});
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
				isMaster: masterNode[0].id === key,
				status: "available",
			})
		);
		for (const node of elasticNodes) {
			const existingNode = await ElasticNode.findOne({ nodeId: node.nodeId });
			if (existingNode) {
				if (existingNode.status !== "upgraded") {
					node.status = existingNode.status;
					node.progress = existingNode.progress;
				}
			}
			if (node.version === clusterInfo.targetVersion) {
				node.status = "upgraded";
				node.progress = 100;
			}

			await ElasticNode.findOneAndUpdate({ nodeId: node.nodeId }, node, {
				new: true,
				runValidators: true,
				upsert: true,
			});
		}
	} catch (error) {
		logger.error("Error syncing nodes from Elasticsearch:", error);
	}
};

export const updateNodeStatus = async (identifier: string, newStatus: string): Promise<IElasticNodeDocument | null> => {
	try {
		// Check if the identifier matches a nodeId or a name
		const query = (await ElasticNode.exists({ nodeId: identifier }))
			? { nodeId: identifier }
			: { name: identifier };

		const updatedNode = await ElasticNode.findOneAndUpdate(
			query,
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

export const updateNodeProgress = async (identifier: string, progress: number) => {
	try {
		const query =
			identifier.includes(".") || identifier.includes(":") ? { nodeId: identifier } : { name: identifier };

		const updatedNode = await ElasticNode.findOneAndUpdate(
			query,
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

export const triggerNodeUpgrade = async (nodeId: string, clusterId: string) => {
	try {
		const node = await getElasticNodeById(nodeId);
		if (!node) {
			return false;
		}
		const clusterInfo = await getClusterInfoById(clusterId);
		const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : "";
		await createAnsibleInventory([node], pathToKey);
		if (!clusterInfo.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password) {
			return false;
		}

		runPlaybookWithLogging("ansible/main.yml", "ansible_inventory.ini", {
			elk_version: clusterInfo.targetVersion,
			username: clusterInfo.elastic.username,
			password: clusterInfo.elastic.password,
		});
		return new Promise((resolve, reject) => resolve(true));
	} catch (error) {
		logger.error(`Error performing upgrade for node with id ${nodeId}`);
		return false;
	}
};

export const triggerUpgradeAll = async (nodes: IElasticNode[], clusterId: string) => {
	try {
		const clusterInfo = await getClusterInfoById(clusterId);
		const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : "";
		await createAnsibleInventory(nodes, pathToKey);
		if (!clusterInfo.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password) {
			return false;
		}

		runPlaybookWithLogging("ansible/main.yml", "ansible_inventory.ini", {
			elk_version: clusterInfo.targetVersion,
			username: clusterInfo.elastic.username,
			password: clusterInfo.elastic.password,
		});
	} catch (error: any) {
		logger.error(`Error performing upgrade for nodes:  ${nodes} because of ${error.message}`);
		throw new Error(`Error performing upgrade for nodes:  ${nodes}`);
	}
};

/// /upgrade (exec)
