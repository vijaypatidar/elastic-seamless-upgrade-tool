package co.hyperflex.services;

import co.hyperflex.dtos.settings.GetSettingResponse;
import co.hyperflex.dtos.settings.UpdateSettingRequest;
import co.hyperflex.dtos.settings.UpdateSettingResponse;
import co.hyperflex.entities.Setting;
import co.hyperflex.repositories.SettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
  private final SettingRepository settingRepository;

  public SettingService(SettingRepository settingRepository) {
    this.settingRepository = settingRepository;
  }

  public GetSettingResponse getSetting() {
    return settingRepository.findById("settings")
        .map(setting -> new GetSettingResponse(setting.getNotificationWebhookUrl()))
        .orElseGet(() -> null);
  }

  public UpdateSettingResponse updateSetting(UpdateSettingRequest request) {
    Setting setting = settingRepository.findById("settings").orElse(new Setting());
    setting.setNotificationWebhookUrl(request.notificationWebhookUrl());
    settingRepository.save(setting);
    return new UpdateSettingResponse(request.notificationWebhookUrl());
  }

}
