import mongoose, { Schema, Document } from "mongoose";

export interface ISettings {
	notificationWebhookUrl?: string;
	id: string;
}

export interface ISettingsDocument extends ISettings, Document {}

const SettingsSchema: Schema<ISettingsDocument> = new Schema<ISettingsDocument>(
	{
		notificationWebhookUrl: { type: String },
		id: {
			type: String,
			default: "settings",
			required: true,
		},
	},
	{ timestamps: true }
);

export const SettingsModel = mongoose.model<ISettingsDocument>("Settings", SettingsSchema, "settings");
