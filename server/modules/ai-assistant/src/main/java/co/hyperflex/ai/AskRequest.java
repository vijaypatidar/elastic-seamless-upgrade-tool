package co.hyperflex.ai;

import com.fasterxml.jackson.databind.JsonNode;

public record AskRequest(
    JsonNode message,
    SessionContext context
) {
}
