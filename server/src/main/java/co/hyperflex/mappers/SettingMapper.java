package co.hyperflex.mappers;

import co.hyperflex.core.services.settings.dtos.GetSettingResponse;
import co.hyperflex.entities.SettingEntity;
import org.springframework.stereotype.Component;

@Component
public class SettingMapper {

  public GetSettingResponse toResponse(SettingEntity setting) {
    return new GetSettingResponse(setting.getNotificationWebhookUrl());
  }
}
