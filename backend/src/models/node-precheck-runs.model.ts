import mongoose, { Schema, Document } from "mongoose";
import { PrecheckStatus } from "../enums";

export interface INodePrecheckRun {
	runId: string;
	nodeName: string;
	precheck: string;
	startedAt: Date;
	endAt?: Date;
	status: PrecheckStatus;
	logs: string[];
}

export interface INodePrecheckRunDocument extends INodePrecheckRun, Document {}

const NodePrecheckRunSchema: Schema<INodePrecheckRunDocument> = new Schema<INodePrecheckRunDocument>(
	{
		runId: { type: String, required: true, unique: true, index: true },
		nodeName: { type: String, required: true },
		precheck: { type: String, required: true },
		startedAt: { type: Date, required: true },
		endAt: { type: Date, required: false },
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
