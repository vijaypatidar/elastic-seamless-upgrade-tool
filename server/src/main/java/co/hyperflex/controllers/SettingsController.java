package co.hyperflex.controllers;

import co.hyperflex.dtos.settings.GetSettingResponse;
import co.hyperflex.dtos.settings.UpdateSettingRequest;
import co.hyperflex.dtos.settings.UpdateSettingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/settings")
public class SettingsController {

  @PostMapping
  public UpdateSettingResponse updateSetting(@RequestBody UpdateSettingRequest request) {
    return new UpdateSettingResponse(request.notificationWebhookUrl());
  }

  @GetMapping
  public GetSettingResponse getSetting() {
    return new GetSettingResponse(null);
  }
}
