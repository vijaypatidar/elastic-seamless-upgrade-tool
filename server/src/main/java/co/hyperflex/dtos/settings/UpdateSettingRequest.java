package co.hyperflex.dtos.settings;

import jakarta.annotation.Nullable;

public record UpdateSettingRequest(
    @Nullable String notificationWebhookUrl
) {
}
