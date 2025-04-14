import logger from "../logger/logger";
import { ISettings, SettingsModel } from "../models/settings.model";

class SettingService {
	settings?: Partial<ISettings>;
	async getSettings() {
		try {
			if (this.settings) {
				return this.settings;
			}
			const settings = await SettingsModel.findOne({ settingsId: "settings" });
			//Cache so we don't have to hit the DB every time for settings
			this.settings = { notificationWebhookUrl: settings?.notificationWebhookUrl };
			return {
				notificationWebhookUrl: settings?.notificationWebhookUrl,
			};
		} catch (err: any) {
			logger.info(err);
			throw err;
		}
	}

	async updateSettings(settings: Partial<ISettings>) {
		const { notificationWebhookUrl } = settings;
		const id = "settings";
		const updateSettings = await SettingsModel.findOneAndUpdate(
			{ settingsId: id },
			{ $set: { notificationWebhookUrl } },
			{ new: true, upsert: true, runValidators: true }
		);
		//Update the cached settings
		this.settings = { notificationWebhookUrl: updateSettings?.notificationWebhookUrl };
		return {
			notificationWebhookUrl: updateSettings?.notificationWebhookUrl,
		};
	}
}

export const settingService = new SettingService();
