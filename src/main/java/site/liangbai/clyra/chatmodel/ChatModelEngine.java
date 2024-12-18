package site.liangbai.clyra.chatmodel;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.lang.Nullable;
import site.liangbai.clyra.chatmodel.frame.ChatTemplate;
import site.liangbai.clyra.chatmodel.frame.ToolTemplate;
import site.liangbai.clyra.chatmodel.service.PromptService;

public class ChatModelEngine {
    @Nullable
    @Resource
    private ChatLanguageModel chatLanguageModel;
    private ChatTemplate chatTemplate;
    @Resource
    private PromptService promptService;
    @Resource
    private ToolTemplate toolTemplate;

    public String transformCommand(String userMessage) {
        return getChatTemplate().generateCommand(promptService.generateModelList(), userMessage);
    }

    public ChatTemplate getChatTemplate() {
        if (chatTemplate == null) {
            chatTemplate = AiServices.builder(ChatTemplate.class)
                    .chatLanguageModel(chatLanguageModel)
                    .tools(toolTemplate)
                    .build();
        }

        return chatTemplate;
    }
}
