import logger from "../logger/logger";
import { IElasticNode } from "../models/cluster-node.model";
import { getClusterInfoById } from "./cluster-info.service";
import { ansibleInventoryService } from "./ansible-inventory.service";
import { ansibleRunnerService } from "./ansible-runner.service";
import { ClusterType, NodeStatus } from "../enums";
import { randomUUID } from "crypto";
import { NotificationEventType, notificationService, NotificationType } from "./notification.service";
import { clusterUpgradeJobService } from "./cluster-upgrade-job.service";
import { getElasticNodeById } from "./elastic-node.service.";
import { getKibanaNodeById, updateKibanaNode, updateKibanaNodeStatus } from "./kibana-node.service";

export const triggerElasticNodeUpgrade = async (nodeId: string, clusterId: string) => {
	try {
		const node = await getElasticNodeById(nodeId);
		if (!node) {
			return false;
		}
		const clusterInfo = await getClusterInfoById(clusterId);
		const clusterUpgradeJob = await clusterUpgradeJobService.getActiveClusterUpgradeJobByClusterId(clusterId);
		const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : "";

		await ansibleInventoryService.createAnsibleInventory([node], { pathToKey, sshUser: clusterInfo.sshUser });
		if (!clusterUpgradeJob.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password) {
			return false;
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
					cluster_type: ClusterType.ELASTIC,
					playbook_run_id: playbookRunId,
					playbook_run_type: "UPGRADE",
					current_version: clusterUpgradeJob.currentVersion,
				},
			})
			.then(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Successful",
					message: "Node has been successfully upgraded to the target version.",
					notificationType: NotificationType.SUCCESS,
					clusterId: clusterId,
				});
			})
			.catch(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Failed",
					message: "An error occurred while upgrading the node. Please check the logs for more details.",
					notificationType: NotificationType.ERROR,
				});
			});

		return new Promise((resolve) => resolve(true));
	} catch (error) {
		logger.error(`Error performing upgrade for node with id ${nodeId}`);
		return false;
	}
};

export const triggerElasticNodesUpgrade = async (nodes: IElasticNode[], clusterId: string) => {
	try {
		const clusterInfo = await getClusterInfoById(clusterId);
		const clusterUpgradeJob = await clusterUpgradeJobService.getActiveClusterUpgradeJobByClusterId(clusterId);
		const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : "";

		await ansibleInventoryService.createAnsibleInventory(nodes, { pathToKey, sshUser: clusterInfo.sshUser });
		if (!clusterUpgradeJob.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password) {
			return false;
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
					cluster_type: ClusterType.ELASTIC,
					playbook_run_id: playbookRunId,
					playbook_run_type: "UPGRADE",
					current_version: clusterUpgradeJob.currentVersion,
				},
			})
			.then(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Successful",
					message: "All nodes have been successfully upgraded to the target version.",
					notificationType: NotificationType.SUCCESS,
					clusterId: clusterId,
				});
			})
			.catch(() => {
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Failed",
					message: "An error occurred while upgrading the nodes. Please check the logs for more details.",
					notificationType: NotificationType.ERROR,
				});
			});
	} catch (error: any) {
		logger.error(`Error performing upgrade for nodes:  ${nodes} because of ${error.message}`);
		throw new Error(`Error performing upgrade for nodes:  ${nodes}`);
	}
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
			.then(async () => {
				await updateKibanaNodeStatus(nodeId, NodeStatus.UPGRADED);
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Successful",
					message: "Kibana node has been successfully upgraded to the target version.",
					notificationType: NotificationType.SUCCESS,
				});
			})
			.catch(async () => {
				await updateKibanaNodeStatus(nodeId, NodeStatus.FAILED);
				notificationService.sendNotification({
					type: NotificationEventType.NOTIFICATION,
					title: "Upgrade Failed",
					message:
						"An error occurred while upgrading the kibana node. Please check the logs for more details.",
					notificationType: NotificationType.ERROR,
					clusterId: clusterId,
				});
			});
	} catch (error) {
		logger.error(`Error performing upgrade for node with id ${nodeId}`);
	}
};
