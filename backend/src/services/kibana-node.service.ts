import logger from "../logger/logger";
import { NodeStatus } from "../enums";
import { clusterUpgradeJobService } from "./cluster-upgrade-job.service";
import { ClusterNode, ClusterNodeType, IKibanaNode } from "../models/cluster-node.model";
import { KibanaClient } from "../clients/kibana.client";

export interface KibanaConfig {
	name: string;
	ip: string;
}

export const createKibanaNodes = async (kibanaConfigs: KibanaConfig[], clusterId: string): Promise<void> => {
	const kibanaClient = await KibanaClient.buildClient(clusterId);
	ClusterNode.collection.deleteMany({ clusterID: clusterId, type: ClusterNodeType.KIBANA });
	for (const kibanaConfig of kibanaConfigs) {
		try {
			const { version, os, roles } = await kibanaClient.getKibanaNodeDetails(kibanaConfig.ip);
			const nodeId = `node-${kibanaConfig.ip}`;
			const progress = 0;
			const status: NodeStatus = NodeStatus.AVAILABLE;
			const kibanaNode: IKibanaNode = {
				nodeId,
				clusterId,
				name: kibanaConfig.name,
				version,
				ip: kibanaConfig.ip,
				roles,
				os,
				progress,
				status,
				type: ClusterNodeType.KIBANA,
			};

			try {
				const clusterUpgradeJob =
					await clusterUpgradeJobService.getActiveClusterUpgradeJobByClusterId(clusterId);
				if (clusterUpgradeJob.targetVersion === version) {
					kibanaNode.status = NodeStatus.UPGRADED;
					kibanaNode.progress = 100;
				}
			} catch (error) {
				logger.debug(`No active upgrade job found:`, error);
			}
			await ClusterNode.findOneAndUpdate(
				{ nodeId: kibanaNode.nodeId, type: ClusterNodeType.KIBANA },
				kibanaNode,
				{
					new: true,
					runValidators: true,
					upsert: true,
				}
			);
		} catch (error) {
			console.error(`Error processing Kibana node ${kibanaConfig.ip}:`, error);
		}
	}
};
