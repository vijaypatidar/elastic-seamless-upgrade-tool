import { Request, Response } from "express";
import logger from "../logger/logger";
import { updateNode } from "../services/elastic-node.service.";
import { updateKibanaNode } from "../services/kibana-node.service";
import {
	AnsibleRequestType,
	AnsibleTaskStatus,
	ClusterType,
	mapAnsibleToPrecheckStatus,
	mapAnsibleToUpgradeStatus,
	NodeStatus,
} from "../enums";
import { updateRunStatus } from "../services/precheck-runs.service";
import { NotificationEventType, notificationService } from "../services/notification.service";

interface HostInfo {
	ip: string;
	name: string;
}
interface BaseAnsibleRequest {
	nodeName: string;
	hosts: HostInfo[];
	clusterType: ClusterType;
	logs?: string[];
	status: AnsibleTaskStatus;
}
export interface AnsibleRequestPrecheck extends BaseAnsibleRequest {
	type: AnsibleRequestType.PRECHECK;
	precheckId: string;
	clusterType: ClusterType;
	precheck: string;
}
export interface AnsibleRequestUpgrade extends BaseAnsibleRequest {
	type: AnsibleRequestType.UPGRADE;
	progress?: number;
	taskName: string;
}

export type AnsibleRequest = AnsibleRequestPrecheck | AnsibleRequestUpgrade;

export const handleAnsibleWebhook = async (req: Request, res: Response) => {
	try {
		logger.info(`Received Ansible webhook:${JSON.stringify(req.body, null, 2)}`);
		// res.sendStatus(200);
		// return;
		const clusterId = req.params.clusterId;
		const body: AnsibleRequest = req.body;
		const { type, nodeName, status } = body;
		if (type === AnsibleRequestType.UPGRADE) {
			const { progress, clusterType, hosts } = body;
			// Handle upgrade request
			const nodeStatus = mapAnsibleToUpgradeStatus(status);
			if (clusterType === ClusterType.ELASTIC) {
				hosts.forEach((host) => {
					updateNode(
						{ ip: host.ip },
						{
							progress: progress,
							status: nodeStatus || NodeStatus.UPGRADING,
						}
					);
				});
			} else {
				updateKibanaNode(
					{ name: nodeName },
					{
						progress: progress,
						status: nodeStatus || NodeStatus.UPGRADING,
					}
				);
			}
		} else {
			//fetch precheck data and update corresponding run
			const { playbookRunId, hosts } = req.body;
			await updateRunStatus(
				{ nodeName: nodeName, playbookRunId: playbookRunId },
				mapAnsibleToPrecheckStatus(status)
			);
		}
		notificationService.sendNotification({
			type: NotificationEventType.UPGRADE_PROGRESS_CHANGE,
		});
		logger.info(
			`Received Ansible webhook for cluster ${clusterId}: ${nodeName} of type ${type}  ${JSON.stringify(req.body)}`
		);
		res.sendStatus(200);
		// Handle the webhook data here
	} catch (error) {
		logger.error(`Error handling Ansible request: ${error}`);
		res.sendStatus(400);
	}
};
