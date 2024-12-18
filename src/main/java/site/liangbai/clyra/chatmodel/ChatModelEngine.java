package site.liangbai.clyra.chatmodel;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import site.liangbai.clyra.chatmodel.frame.ChatTemplate;
import site.liangbai.clyra.chatmodel.frame.ToolTemplate;
import site.liangbai.clyra.chatmodel.service.PromptService;

@Component
public class ChatModelEngine {
    @Nullable
    @Resource
    private ChatLanguageModel chatLanguageModel;
    private ChatTemplate chatTemplate;
    @Resource
    private PromptService promptService;

    public String transformCommand(String userMessage) {
        return getChatTemplate().generateCommand(promptService.generateModelList(), userMessage);
    }

    public ChatTemplate getChatTemplate() {
        if (chatTemplate == null) {
            chatTemplate = AiServices.builder(ChatTemplate.class)
                    .chatLanguageModel(chatLanguageModel)
                    .tools(new ToolTemplate())
                    .build();
        }

        return chatTemplate;
    }
}
