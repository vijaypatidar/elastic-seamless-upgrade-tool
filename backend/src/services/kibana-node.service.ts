import logger from "../logger/logger";
import { getClusterInfoById } from "./cluster-info.service";
import { ansibleInventoryService } from "./ansible-inventory.service";
import { ansibleRunnerService } from "./ansible-runner.service";
import { ClusterType, NodeStatus } from "../enums";
import { randomUUID } from "crypto";
import { NotificationEventType, notificationService, NotificationType } from "./notification.service";
import { clusterUpgradeJobService } from "./cluster-upgrade-job.service";
import { ClusterNode, ClusterNodeType, IClusterNodeDocument, IKibanaNode } from "../models/cluster-node.model";
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
			const { version, os, roles } = await kibanaClient.getKibanaNodeDetails();
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

export const getKibanaNodes = async (clusterId: string) => {
	try {
		return await ClusterNode.find({ clusterId: clusterId, type: ClusterNodeType.KIBANA });
	} catch (error) {
		throw new Error("Unable to fetch kibana nodes");
	}
};

export const updateKibanaNodeStatus = async (
	nodeId: string,
	newStatus: string
): Promise<IClusterNodeDocument | null> => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			{ nodeId, type: ClusterNodeType.KIBANA },
			{ status: newStatus },
			{ new: true, runValidators: true }
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

export const updateKibanaNodeProgress = async (nodeId: string, progress: number) => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			{ nodeId, type: ClusterNodeType.KIBANA },
			{ progress: progress },
			{ new: true, runValidators: true }
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

export const updateKibanaNode = async (identifier: Record<string, any>, updatedNodeValues: Partial<IKibanaNode>) => {
	try {
		const updatedNode = await ClusterNode.findOneAndUpdate(
			{ ...identifier, type: ClusterNodeType.KIBANA },
			{ $set: updatedNodeValues },
			{ new: true }
		);
		if (!updatedNode) {
			throw new Error(`Node with identifier ${identifier} not found`);
		}
	} catch (error) {
		throw new Error(`Error updating node: ${error}`);
	}
};

export const getKibanaNodeById = async (nodeId: string): Promise<IKibanaNode | null> => {
	const kibanaNode = await ClusterNode.findOne({ nodeId: nodeId, type: ClusterNodeType.KIBANA });
	if (!kibanaNode || kibanaNode.type !== ClusterNodeType.KIBANA) return null;
	return kibanaNode;
};

export const triggerKibanaNodeUpgrade = async (nodeId: string, clusterId: string) => {
	try {
		const node = await getKibanaNodeById(nodeId);
		if (!node) {
			logger.error(`Kibana node not found for [nodeId:${nodeId}]`);
			return;
		}
		const clusterInfo = await getClusterInfoById(clusterId);
		const clusterUpgradeJob = await clusterUpgradeJobService.getActiveClusterUpgradeJobByClusterId(clusterId);
		const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : ""; //Should be stored in clusterInfo
		await ansibleInventoryService.createAnsibleInventoryForKibana([node], {
			pathToKey,
			sshUser: clusterInfo.sshUser,
		});
		if (!clusterInfo.elastic.username || !clusterInfo.elastic.password) {
			return;
		}
		const playbookRunId = randomUUID();

		ansibleRunnerService
			.runPlaybook({
				playbookPath: "playbooks/main.yml",
				inventoryPath: "ansible_inventory.ini",
				variables: {
					elk_version: clusterUpgradeJob.targetVersion,
					es_username: clusterInfo.elastic.username,
					es_password: clusterInfo.elastic.password,
					elasticsearch_uri: clusterInfo.elastic.url,
					cluster_type: ClusterType.KIBANA,
					playbook_run_id: playbookRunId,
					playbook_run_type: "UPGRADE",
					current_version: clusterUpgradeJob.currentVersion,
				},
			})
			.then(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Successful",
					message: "Kibana node has been successfully upgraded to the target version.",
					notificationType: NotificationType.SUCCESS,
				});
			})
			.catch(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Failed",
					message:
						"An error occurred while upgrading the kibana node. Please check the logs for more details.",
					notificationType: NotificationType.ERROR,
				});
			});
	} catch (error) {
		logger.error(`Error performing upgrade for node with id ${nodeId}`);
	}
};

export const syncKibanaNodes = async (clusterId: string) => {
	const kibanaClient = await KibanaClient.buildClient(clusterId);
	try {
		const kibanaNodes = await ClusterNode.find({ clusterId: clusterId, type: ClusterNodeType.KIBANA });
		for (const kibanaNode of kibanaNodes) {
			const { version, os, roles } = await kibanaClient.getKibanaNodeDetails();
			kibanaNode.version = version;
			kibanaNode.os = os;
			kibanaNode.roles = roles;
			await ClusterNode.findOneAndUpdate(
				{ nodeId: kibanaNode.nodeId, type: ClusterNodeType.KIBANA },
				kibanaNode,
				{
					new: true,
					runValidators: true,
					upsert: true,
				}
			);
		}
	} catch (error: any) {
		throw new Error(`Unable to sync kibana nodes ${error.message}`);
	}
};
