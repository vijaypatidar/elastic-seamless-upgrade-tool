import { Request, Response } from "express";
import { getAllElasticNodes, getElasticNodeById } from "../services/elastic-node.service.";
import { getLatestRunsByPrecheck, runPrecheck } from "../services/precheck-runs.service";
import { ELASTIC_PRECHECK_CONFIG } from "../config/precheck-config";
import { IElasticNode } from "../models/elastic-node.model";

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
