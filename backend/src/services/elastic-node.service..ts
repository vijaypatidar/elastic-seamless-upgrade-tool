import { ElasticClient } from "../clients/elastic.client";
import logger from "../logger/logger";
import { ClusterNode, IElasticNode, ClusterNodeType } from "../models/cluster-node.model";
import { NodeStatus } from "../enums";
import { clusterNodeService } from "./cluster-node.service";

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
			const existingNode = await clusterNodeService.getElasticNodeById(node.nodeId);
			// const existingNode = await ClusterNode.findOne({ nodeId: node.nodeId, type: ClusterNodeType.ELASTIC });
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
