package co.hyperflex.dtos.settings;

import jakarta.annotation.Nullable;

public record UpdateSettingResponse(
    @Nullable String notificationWebhookUrl
) {
}
