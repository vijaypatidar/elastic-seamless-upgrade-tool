import { PrecheckStatus } from "../enums";
import logger from "../logger/logger";
import NodePrecheckRun, { INodePrecheckRun, INodePrecheckRunDocument } from "../models/node-precheck-runs.model";

export const getRunByName = async (precheck: string, nodeName: string): Promise<INodePrecheckRun | null> => {
	const nodePrecheckRun = await NodePrecheckRun.findOne({ nodeName, precheck }).sort({ createdAt: -1 });
	if (!nodePrecheckRun) return null;
	return nodePrecheckRun;
};

export const getLatestRunsByPrecheck = async (precheck: string): Promise<INodePrecheckRunDocument[]> => {
	try {
		const results = await NodePrecheckRun.aggregate<INodePrecheckRunDocument>([
			{ $match: { precheck: precheck } },
			{ $sort: { createdAt: -1 } },
			{
				$group: {
					_id: "$nodeName",
					doc: { $first: "$$ROOT" },
				},
			},
			{
				$replaceRoot: {
					newRoot: "$doc",
				},
			},
		]);
		return results;
	} catch (error: any) {
		logger.error(`Error getting latest run: ${error.message}`);
		throw new Error(`Error getting latest run: ${error.message}`);
	}
};

export const updateRunStatus = async (identifier: Record<string, any>, newStatus: PrecheckStatus): Promise<void> => {
	try {
		let updatedNode = null;
		if (newStatus === PrecheckStatus.COMPLETED || newStatus === PrecheckStatus.FAILED) {
			updatedNode = await NodePrecheckRun.findOneAndUpdate(
				identifier,
				{ status: newStatus, endAt: Date.now() },
				{ new: true, runValidators: true }
			);
		} else {
			updatedNode = await NodePrecheckRun.findOneAndUpdate(
				identifier,
				{ status: newStatus },
				{ new: true, runValidators: true }
			);
		}
		if (!updatedNode) {
			logger.debug(`Node with identifier ${identifier} not found.`);
			return;
		}
	} catch (error: any) {
		console.error(`Error updating status for node ${identifier}: ${error.message}`);
		throw error;
	}
};
