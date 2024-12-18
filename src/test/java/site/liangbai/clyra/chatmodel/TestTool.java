package site.liangbai.clyra.chatmodel;

import dev.langchain4j.agent.tool.Tool;

public class TestTool {
    @Tool("获取模块modelName的节点信息")
    public String getModelInfo(String modelName) {
        if (!modelName.equalsIgnoreCase("sq")) {
            System.out.println("get: " + modelName);
            return "";
        }

        return """
                {
                                nodes: [
                                    {
                                        "name": "queryall",
                                        "args": "",
                                        "args_info": [],
                                        "desc": "查询个人所有授权信息"
                                    },
                                    {
                                        "name": "query <id>",
                                        "args": "<id>",
                                        "args_info": [
                                            {
                                                "name": "id",
                                                "desc": "用户id，为一串数字"\s
                                            },
                                            {
                                                "name": "<time>(D|M|H|S)",
                                                "desc": "时间，最后加上D(天), M(月), H(时), S(秒)中的一个字母"
                                            }
                                        ]
                                        "desc": "查询指定id用户的授权信息"
                                    },
                                    {
                                        "name": "add",
                                        "args": "<id> <time>",
                                        "args_info": [
                                            {
                                                "name": "id",
                                                "desc": "用户id，为一串数字"\s
                                            },
                                            {
                                                "name": "<time>(D|M|H|S)",
                                                "desc": "时间，最后加上D(天), M(月), H(时), S(秒)中的一个字母"
                                            }
                                        ],
                                        "desc": "为指定id用户添加一定time的授权信息"
                                    }
                                ]
                            }
                """;
    }
}
