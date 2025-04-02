import mongoose, { Schema, Document } from "mongoose";

export enum PrecheckStatus {
	PENDING = "PENDING",
	RUNNING = "RUNNING",
	FAILED = "FAILED",
	COMPLETED = "COMPLETED",
}
export interface INodePrecheckRun {
	nodeId: string;
	precheckId: string;
	startedAt: Date;
	endAt?: Date;
	status: PrecheckStatus;
	logs: string[];
}

export interface INodePrecheckRunDocument extends INodePrecheckRun, Document {}

const NodePrecheckRunSchema: Schema<INodePrecheckRunDocument> = new Schema<INodePrecheckRunDocument>(
	{
		nodeId: { type: String, required: true },
		precheckId: { type: String, required: true },
		startedAt: { type: Date, required: true },
		endAt: { type: String, required: false },
		logs: { type: [String], required: true },
		status: {
			type: String,
			required: true,
			enum: [PrecheckStatus.PENDING, PrecheckStatus.RUNNING, PrecheckStatus.FAILED, PrecheckStatus.COMPLETED],
			default: PrecheckStatus.PENDING,
		},
	},

	{ timestamps: true }
);

const NodePrecheckRun = mongoose.model<INodePrecheckRunDocument>("NodePrecheckRun", NodePrecheckRunSchema);

export default NodePrecheckRun;
