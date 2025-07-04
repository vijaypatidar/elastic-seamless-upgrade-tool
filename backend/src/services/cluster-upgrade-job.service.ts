import { randomUUID } from "crypto";
import { ConflictError, NotFoundError } from "../errors";
import { ClusterUpgradeJob, IClusterUpgradeJob } from "../models/cluster-upgrade-job.model";
import { ClusterNode } from "../models/cluster-node.model";
import { NodeStatus } from "../enums";
class ClusterUpgradeJobService {
	async getActiveClusterUpgradeJobByClusterId(clusterId: string): Promise<IClusterUpgradeJob> {
		const job = await ClusterUpgradeJob.findOne({ clusterId, status: { $ne: "completed" } });
		if (!job) {
			throw new NotFoundError(`No upgrade job found for clusterId: ${clusterId}`);
		}
		return job;
	}

	async getLatestClusterUpgradeJobByClusterId(clusterId: string): Promise<IClusterUpgradeJob | null> {
		return await ClusterUpgradeJob.findOne({ clusterId }).sort({ createdAt: -1 });
	}

	async getClusterUpgradeJobByJobId(jobId: string): Promise<IClusterUpgradeJob> {
		const job = await ClusterUpgradeJob.findOne({ jobId: jobId });
		if (!job) {
			throw new NotFoundError(`No upgrade job found for jobId: ${jobId}`);
		}
		return job;
	}

	async createClusterUpgradeJob(jobData: Omit<IClusterUpgradeJob, "jobId" | "status">): Promise<IClusterUpgradeJob> {
		const existingJob = await ClusterUpgradeJob.findOne({
			clusterId: jobData.clusterId,
			status: { $ne: "completed" },
		});
		if (existingJob) {
			throw new ConflictError(`An active upgrade job already exists for clusterId: ${jobData.clusterId}`);
		}
		const newJob = new ClusterUpgradeJob({
			jobId: randomUUID(),
			clusterId: jobData.clusterId,
			currentVersion: jobData.currentVersion,
			targetVersion: jobData.targetVersion,
			status: "pending",
			createdAt: new Date(),
			updatedAt: new Date(),
		});
		await newJob.save();
		await ClusterNode.updateMany(
			{ clusterId: jobData.clusterId },
			{ $set: { status: NodeStatus.AVAILABLE, progress: 0 } }
		);
		return newJob;
	}

	async updateClusterUpgradeJob(
		identifier: Record<string, any>,
		updatedJobValues: Partial<IClusterUpgradeJob>
	): Promise<IClusterUpgradeJob> {
		const updatedJob = await ClusterUpgradeJob.findOneAndUpdate(
			identifier,
			{ $set: updatedJobValues },
			{ new: true }
		);
		if (!updatedJob) {
			throw new NotFoundError(`Cluster upgrade job with identifier ${JSON.stringify(identifier)} not found`);
		}
		return updatedJob;
	}
}

export const clusterUpgradeJobService = new ClusterUpgradeJobService();
