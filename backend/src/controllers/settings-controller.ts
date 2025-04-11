import { Request, Response } from "express";
import logger from "../logger/logger";
import { ISettings, SettingsModel } from "../models/settings.model";

export const updateSettings = async (req: Request, res: Response) => {
	try {
		const { notificationWebhookUrl }: ISettings = req.body;
		const id = "settings";
		await SettingsModel.findOneAndUpdate(
			{ settingsId: id },
			{ $set: { notificationWebhookUrl } },
			{ new: true, upsert: true, runValidators: true }
		);
		res.send({ message: "Settings updated" });
	} catch (err: any) {
		logger.info(err);
		res.status(400).send({ err: err.message });
	}
};

export const getSettings = async (req: Request, res: Response) => {
	try {
		const settings = await SettingsModel.findOne({ settingsId: "settings" });
		res.send({
			notificationWebhookUrl: settings?.notificationWebhookUrl,
		});
	} catch (err: any) {
		logger.info(err);
		res.status(400).send({ err: err.message });
	}
};
