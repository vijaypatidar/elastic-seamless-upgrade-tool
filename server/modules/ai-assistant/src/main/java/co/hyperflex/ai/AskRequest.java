package co.hyperflex.ai;

import com.fasterxml.jackson.databind.JsonNode;

public class AskRequest {
  private JsonNode message;
  private JsonNode context;

  public JsonNode getMessage() {
    return message;
  }

  public void setMessage(JsonNode message) {
    this.message = message;
  }

  public JsonNode getContext() {
    return context;
  }

  public void setContext(JsonNode context) {
    this.context = context;
  }
}
