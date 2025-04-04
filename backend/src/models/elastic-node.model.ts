import mongoose, { Schema, Document } from "mongoose";


export enum NodeStatus{
	UPGRADING = "UPGRADING",
	AVAILABLE = "AVAILABLE",
	UPGRADED = "UPGRADED",
	FAILED = "FAILED"
}

export interface IElasticNode {
	nodeId: string;
	clusterId: string;
	name: string;
	version: string;
	ip: string;
	roles: string[];
	os: Record<string, any>;
	isMaster: boolean;
	progress: Number;
	status: NodeStatus
}

export interface IElasticNodeDocument extends IElasticNode, Document {}

const ElasticNodeSchema: Schema<IElasticNodeDocument> = new Schema<IElasticNodeDocument>(
	{
		nodeId: { type: String, required: true, unique: true, index: true },
		clusterId: { type: String, required: true, index: true },
		name: { type: String, required: true, unique: true },
		version: { type: String, required: true },
		ip: { type: String, required: true },
		roles: { type: [String], required: true },
		os: { type: Object, required: true },
		isMaster: { type: Boolean, required: true },
		progress: { type: Number, required: false },
		status: {
			type: String,
			required: true,
			enum: Object.values(NodeStatus),
			default: NodeStatus.AVAILABLE,
		},
	},
	{ timestamps: true }
);

const ElasticNode = mongoose.model<IElasticNodeDocument>("ElasticNode", ElasticNodeSchema);

export default ElasticNode;
