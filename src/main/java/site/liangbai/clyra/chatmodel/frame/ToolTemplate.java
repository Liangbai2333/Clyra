package site.liangbai.clyra.chatmodel.frame;

import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import site.liangbai.clyra.chatmodel.service.PromptService;

public class ToolTemplate {
    @Resource
    private PromptService promptService;

    @Tool("获取模块modelName的节点信息")
    public String getModelInfo(String modelName) {
        return promptService.generateCommandDetails(modelName);
    }
}
