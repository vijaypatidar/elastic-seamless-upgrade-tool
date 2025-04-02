import mongoose, { Schema, Document } from "mongoose";

export interface IPrecheck {
	name: string;
	description: string;
}

export interface IPrecheckDocument extends IPrecheck, Document {}

const PrecheckSchema: Schema<IPrecheckDocument> = new Schema<IPrecheckDocument>(
	{
		name: { type: String, required: true, unique: true },
		description: { type: String, required: true, unique: true },
	},
	{ timestamps: true }
);

const Precheck = mongoose.model<IPrecheckDocument>("Precheck", PrecheckSchema);

export default Precheck;
