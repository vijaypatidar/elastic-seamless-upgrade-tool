package co.hyperflex.controllers;

import co.hyperflex.ai.AskRequest;
import co.hyperflex.ai.Assistant;
import co.hyperflex.breakingchanges.BreakingChangeRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai-assistant")
public class AiAssistantController {
  private final Assistant assistant;
  private final BreakingChangeRepository breakingChangeRepository;
  private final Map<Integer, String> contextMap = new HashMap<>();

  public AiAssistantController(Assistant assistant, BreakingChangeRepository breakingChangeRepository) {
    this.assistant = assistant;
    this.breakingChangeRepository = breakingChangeRepository;
  }

  @PostMapping("/ask")
  public String ask(@RequestBody AskRequest request) {
    var message = request.getMessage().asText();
    if (request.getContext() != null && request.getContext().has("precheckId")) {
      var precheckId = request.getContext().get("precheckId").asText();
      if (!contextMap.containsKey(1) || !precheckId.equals(contextMap.get(1))) {
        var change = breakingChangeRepository.findById(precheckId).orElseThrow();

        message =
            """
            # Breaking Change Context
            - **Title:** %s
            - **Description:** %s
            - **Source:** %s
        
            # User Query
            %s
            """.formatted(
                change.getTitle(),
                change.getDescription(),
                change.getUrl(),
                message
            );

      }
    }
    return assistant.chat(1, message);
  }
}
