package co.hyperflex.ai;


import static co.hyperflex.ai.Prompts.ELASTIC_UPGRADE_BREAKING_CHANGE_PROMPT;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

  @SystemMessage(ELASTIC_UPGRADE_BREAKING_CHANGE_PROMPT)
  String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
