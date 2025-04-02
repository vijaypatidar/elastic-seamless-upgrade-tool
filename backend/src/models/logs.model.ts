import mongoose, { Schema, Document } from "mongoose"

export interface ILog {
	clusterId: string
	nodeId: string
	timestamp: Date
	message: string
}

export interface ILogDocument extends ILog, Document {}

const ClusterInfoSchema: Schema<ILogDocument> = new Schema<ILogDocument>(
	{
		clusterId: { type: String, required: true },
		nodeId: { type: String, required: true },
		timestamp: { type: Date, required: true },
		message: { type: String, required: true },
	},
	{ timestamps: true }
)

// Create the model
const Log = mongoose.model<ILogDocument>("Log", ClusterInfoSchema)

export default Log
