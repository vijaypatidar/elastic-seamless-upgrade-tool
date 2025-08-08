package co.hyperflex.dtos.settings;

import jakarta.annotation.Nullable;

public record GetSettingResponse(
    @Nullable String notificationWebhookUrl
) {
}
