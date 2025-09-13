package co.hyperflex.ai.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

  @Bean
  public ChatModel chatModel() {
    return OllamaChatModel.builder()
        .baseUrl("http://192.168.1.64:11434")
        .modelName("llama3.1:8b")
        .logRequests(true)
        .timeout(Duration.ofDays(1))
        .think(false)
        .returnThinking(false)
        .build();
  }
}
