import { Request, Response } from "express";
import logger from "../logger/logger";
import { updateNode } from "../services/elastic-node.service.";
import { updateKibanaNode } from "../services/kibana-node.service";
import { AnsibleRequestType, AnsibleTaskStatus, ClusterType, mapAnsibleToPrecheckStatus, NodeStatus } from "../enums";
import { updateRunStatus } from "../services/precheck-runs.service";

interface BaseAnsibleRequest {
	nodeName: string;
	logs?: string[];
}
export interface AnsibleRequestPrecheck extends BaseAnsibleRequest {
	type: AnsibleRequestType.PRECHECK;
	clusterType: ClusterType;
	precheck: string;
	status: AnsibleTaskStatus;
}
export interface AnsibleRequestUpgrade extends BaseAnsibleRequest {
	type: AnsibleRequestType.UPGRADE;
	status: NodeStatus;
	progress?: number;
	taskName: string;
}

export type AnsibleRequest = AnsibleRequestPrecheck | AnsibleRequestUpgrade;
export const handleAnsibleWebhook = async (req: Request, res: Response) => {
	try {
		const clusterId = req.params.clusterId;
		const { type, nodeName, status }: AnsibleRequest = req.body;
		if (type === AnsibleRequestType.UPGRADE) {
			const { progress, taskName, clusterType } = req.body;
			// Handle upgrade request
			if (clusterType === ClusterType.ELASTIC) {
				updateNode(
					{ name: nodeName },
					{
						progress: progress,
						status: (status as NodeStatus) || NodeStatus.UPGRADING,
					}
				);
			} else {
				updateKibanaNode(
					{ name: nodeName },
					{
						progress: progress,
						status: (status as NodeStatus) || NodeStatus.UPGRADING,
					}
				);
			}
		} else {
			const { precheck } = req.body;
			await updateRunStatus({ nodeName: nodeName, precheck: precheck }, mapAnsibleToPrecheckStatus(status));
		}
		logger.info(
			`Received Ansible webhook for cluster ${clusterId}: ${nodeName} of type ${type}  ${JSON.stringify(req.body)}`
		);
		res.sendStatus(200);
	} catch (error) {
		logger.error(`Error handling Ansible request: ${error}`);
		res.sendStatus(400);
	}
};
