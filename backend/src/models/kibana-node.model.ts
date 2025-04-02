import mongoose, { Schema, Document } from "mongoose"

export interface IKibanaNode {
	nodeId: string
	clusterId: string
	name: string
	version: string
	ip: string
	roles: string[]
	os: Record<string, any>
	progress: Number
	status: "available" | "upgrading" | "upgraded" | "failed"
}
export interface KibanaConfig {
	name: string
	ip: string
}

export interface IKibanaNodeDocument extends IKibanaNode, Document {}

const KibanaNodeSchema: Schema<IKibanaNodeDocument> = new Schema<IKibanaNodeDocument>(
	{
		nodeId: { type: String, required: true, unique: true },
		clusterId: { type: String, required: true },
		name: { type: String, required: true, unique: true },
		version: { type: String, required: true },
		ip: { type: String, required: true },
		roles: { type: [String], required: true },
		os: { type: Object, required: true },
		progress: { type: Number, required: false },
		status: {
			type: String,
			enum: ["available", "upgrading", "upgraded", "failed"],
			default: "available",
		},
	},
	{ timestamps: true }
)

const KibanaNode = mongoose.model<IKibanaNodeDocument>("KibanaNode", KibanaNodeSchema)

export default KibanaNode
