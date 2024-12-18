package site.liangbai.clyra.chatmodel;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import site.liangbai.clyra.chatmodel.frame.ChatTemplate;

public class ChatTest {
    @Test
    public void testChat() {
        System.out.println(System.getenv());
        String key = System.getenv("SFLOW_KEY");
        String baseUrl1 = "https://api.siliconflow.cn/v1";

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(key)
                .baseUrl(baseUrl1)
                .modelName("Qwen/Qwen2.5-14B-Instruct")
                .build();

        ChatTemplate chatTemplate = AiServices.builder(ChatTemplate.class)
                .chatLanguageModel(chatModel)
                .tools(new TestTool())
                .build();

        System.out.println(chatTemplate.generateCommand("""
                [
                    {
                        "name": "sq",
                        "desc": "关于授权系统，如查询添加删除授权信息相关请求"
                    },
                    {
                        "name": "light",
                        "desc": "控制灯光的开与关"
                    }
                ]
                """, "我要给123456添加30天的授权"));
    }
}
