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

interface HostInfoUpgrade {
	ip: string;
	name: string;
	progress: number;
}
interface HostInfoPrecheck {
	ip: string;
	name: string;
	progress: number;
	precheckId: string;
}

interface BaseAnsibleRequest {
	clusterType: ClusterType;
	logs?: string[];
	status: AnsibleTaskStatus;
	playbookRunId: string;
}
export interface AnsibleRequestPrecheck extends BaseAnsibleRequest {
	type: AnsibleRequestType.PRECHECK;
	precheckId: string;
	clusterType: ClusterType;
	hosts: HostInfoPrecheck[];
	precheck: string;
}
export interface AnsibleRequestUpgrade extends BaseAnsibleRequest {
	type: AnsibleRequestType.UPGRADE;
	progress?: number;
	hosts: HostInfoUpgrade[];
	taskName: string;
}

export type AnsibleRequest = AnsibleRequestPrecheck | AnsibleRequestUpgrade;

export const handleAnsibleWebhook = async (req: Request, res: Response) => {
	try {
		const body: AnsibleRequest = req.body;
		const { type, status, playbookRunId } = body;
		logger.info(
			`Received Ansible webhook [playbookRunId: ${playbookRunId}] of [type: ${type}]  [Hook Body: ${JSON.stringify(req.body, null, 2)}]`
		);
		if (type === AnsibleRequestType.UPGRADE) {
			const { clusterType, hosts } = body;
			// Handle upgrade request
			const nodeStatus = mapAnsibleToUpgradeStatus(status);
			if (clusterType === ClusterType.ELASTIC) {
				hosts.forEach((host) => {
					updateNode(
						{ ip: host.ip },
						{
							progress: host.progress,
							status: nodeStatus || NodeStatus.UPGRADING,
						}
					);
				});
			} else {
				hosts.forEach((host) => {
					updateKibanaNode(
						{ ip: host.ip },
						{
							progress: host.progress,
							status: nodeStatus || NodeStatus.UPGRADING,
						}
					);
				});
			}
		} else {
			//fetch precheck data and update corresponding run
			const { playbookRunId, hosts } = body;
			hosts.forEach((host) => {
				const { precheckId } = host;
				updateRunStatus(
					{ precheckRunId: playbookRunId, precheckId: precheckId },
					mapAnsibleToPrecheckStatus(status)
				);
			});
		}
		notificationService.sendNotification({
			type: NotificationEventType.UPGRADE_PROGRESS_CHANGE,
		});
		res.sendStatus(200);
		// Handle the webhook data here
	} catch (error) {
		logger.error(`Error handling Ansible request: ${error}`);
		res.sendStatus(400);
	}
};
