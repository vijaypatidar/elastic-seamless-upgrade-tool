import { Request, Response } from "express";
import { getAllElasticNodes, getElasticNodeById } from "../services/elastic-node.service.";
import { getLatestRunsByPrecheck, runPrecheck } from "../services/precheck-runs.service";
import { ELASTIC_PRECHECK_CONFIG } from "../config/precheck-config";
import { PrecheckStatus } from "../enums";

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
	if (!precheckRuns || precheckRuns.length === 0) {
		res.status(404).send({ message: "No precheck runs found" });
		return;
	}
	const groupedPrecheckRuns = precheckRuns.reduce<Record<string, typeof precheckRuns>>((acc, run) => {
		if (!acc[run.precheckId]) {
			acc[run.precheckId] = [];
		}
		acc[run.precheckId].push(run);
		return acc;
	}, {});
	const response = ELASTIC_PRECHECK_CONFIG.individuals.map((precheck) => {
		const precheckRuns = groupedPrecheckRuns[precheck.id];
		const status = (() => {
			let hasCompleted = false;
			let hasPending = false;

			for (const run of precheckRuns) {
				if (run.status === PrecheckStatus.FAILED) return PrecheckStatus.FAILED;
				if (run.status === PrecheckStatus.RUNNING) return PrecheckStatus.RUNNING;
				if (run.status === PrecheckStatus.PENDING) hasPending = true;
				if (run.status === PrecheckStatus.COMPLETED) hasCompleted = true;
			}

			if (hasPending && hasCompleted) return PrecheckStatus.RUNNING;
			if (hasPending) return PrecheckStatus.PENDING;
			return PrecheckStatus.COMPLETED;
		})();
		return {
			id: precheck.id,
			name: precheck.name,
			status: status,
			nodes: precheckRuns,
		};
	});
	res.send(response);
};
