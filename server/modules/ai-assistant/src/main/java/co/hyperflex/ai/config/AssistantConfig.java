package co.hyperflex.ai.config;


import static co.hyperflex.ai.Prompts.ELASTIC_UPGRADE_PROMPT;

import co.hyperflex.ai.Assistant;
import co.hyperflex.ai.Tools;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {


  @Bean
  public SystemMessage elasticAssistantSystemPrompt() {
    return new SystemMessage(ELASTIC_UPGRADE_PROMPT);
  }

  @Bean
  public Assistant assistant(ChatModel model, Tools tools) {
    return AiServices
        .builder(Assistant.class)
        .chatModel(model)
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
        .tools(tools)
        .build();
  }
}
