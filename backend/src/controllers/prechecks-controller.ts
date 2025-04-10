import { randomUUID } from "crypto";
import { Request, Response } from "express";
import { getAllElasticNodes, getElasticNodeById } from "../services/elastic-node.service.";
import { ansibleInventoryService } from "../services/ansible-inventory.service";
import NodePrecheckRun, { INodePrecheckRun } from "../models/node-precheck-runs.model";
import { PrecheckStatus } from "../enums";
import { IElasticNode } from "../models/elastic-node.model";
import { addPrecheckRunJobs, getLatestRunsByPrecheck, PrecheckRunJob } from "../services/precheck-runs.service";
import { ELASTIC_PRECHECK_CONFIG } from "../config/precheck-config";
import { getClusterInfoById } from "../services/cluster-info.service";

const runPrecheck = async (precheckIds: string[], nodes: IElasticNode[], clusterId: string) => {
	const runId = randomUUID();
	const clusterInfo = await getClusterInfoById(clusterId);
	if (!clusterInfo.pathToKey) {
		throw new Error("Cluster info not found or path to key is missing");
	}

	const startedAt = new Date();
	const precheckRuns: INodePrecheckRun[] = nodes
		.map((node) => ({
			ip: node.ip,
			nodeId: node.nodeId,
			precheckRunId: runId,
			startedAt: startedAt,
			status: PrecheckStatus.PENDING,
			logs: [],
			clusterId: clusterId,
		}))
		.map((precheck) => {
			return precheckIds.map((precheckId) => ({
				...precheck,
				precheckId,
			}));
		})
		.flat();
	await NodePrecheckRun.insertMany(precheckRuns);

	Promise.all(
		precheckRuns.map(async (precheckRun) => {
			const inventoryFileName = `${precheckRun.precheckRunId}-${precheckRun.ip}.ini`;
			await ansibleInventoryService.createInventoryForPrecheckPerNode({
				node: { ip: precheckRun.ip, name: precheckRun.ip },
				iniName: inventoryFileName,
				pathToKey: clusterInfo.pathToKey!!,
			});
			const precheckRunJob: PrecheckRunJob = {
				precheckId: precheckRun.precheckId,
				clusterId: clusterId,
				playbookRunId: precheckRun.precheckRunId,
				inventoryPath: `ini/${inventoryFileName}`,
			};
			return precheckRunJob;
		})
	).then((precheckRuns) => {
		addPrecheckRunJobs(precheckRuns);
	});
	return runId;
};

export const runAllPrecheksHandler = async (req: Request, res: Response) => {
	const { clusterId } = req.params;
	const nodes = await getAllElasticNodes(clusterId);
	const precheckIds: string[] = ELASTIC_PRECHECK_CONFIG.individuals.map((precheck) => precheck.id);
	const runId = await runPrecheck(precheckIds, nodes, clusterId);
	res.send({ message: "Prechecks started", runId });
};

export const runPrechekByNodeIdHandler = async (req: Request, res: Response) => {
	const { clusterId, precheckId, nodeId } = req.params;
	const node = await getElasticNodeById(nodeId);
	if (!node) {
		res.status(404).send({ message: "Node not found" });
		return;
	}

	const selectedPrecheck = ELASTIC_PRECHECK_CONFIG.individuals.filter((precheck) => precheckId === precheck.id);
	if (!selectedPrecheck) {
		res.status(404).send({ message: "Precheck not found" });
		return;
	}
	const runId = await runPrecheck([precheckId], [node], clusterId);

	res.send({ message: "Prechecks started", runId });
};

export const getPrecheckRunByClusterIdHandler = async (req: Request, res: Response) => {
	const { clusterId } = req.params;
	const precheckRuns = await getLatestRunsByPrecheck(clusterId);
	res.send(precheckRuns);
};
