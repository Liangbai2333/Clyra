package site.liangbai.clyra.chatmodel.frame;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChatTemplate {
    @SystemMessage("""
                    根据用户输入描述选择模块(如果找不到对应模块，返回null。如果用户需求超过1个, 返回busy)，并调用getModelInfo(模块名称)工具获取模块的节点信息。提取并检索以下内容：
                    - `module_name`：模块名称
                    - `node_name`：节点名称
                    - `node_args`：节点参数，必须为具体的参数值，不携带参数名，仅包含实际内容，参数之间用空格隔开，且不得包含任何模板字符（如 `<id>`）。
                    输出格式：`module_name node_name node_args`
                    
                    模块列表：
                    {{models}}
                    
                    执行过程：
                    1. 根据输入描述选择对应的模块名称。
                    2. 调用 `getModelInfo(模块名称)` 获取该模块的节点信息。
                    3. 从返回的节点中选择一个并输出：`module_name node_name node_args`。
                    
                    严格按照格式输出，不要输出其他任何内容，严格按照执行过程
                    """)
    String generateCommand(@V("models") String models, @UserMessage String userInput);
}
