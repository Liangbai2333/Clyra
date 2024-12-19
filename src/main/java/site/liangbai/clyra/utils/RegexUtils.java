package site.liangbai.clyra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private static final String regex = "\\b\\w+\\s*=\\s*\"(\\S+)\"\\s*|" +   // id="123"
            "\\b\\w+\\s*=\\s*(\\S+)\\s*|" +       // id=123
            "\\b\\w+:\\s*\"(\\S+)\"\\s*|" +       // id: "123"
            "\\b\\w+:\\s*(\\S+)\\s*|" +           // id: 123
            "<(\\w+)>\\s*(\\S+)\\s*</\\w+>|" +    // <id>123</id>
            "<(\\w+)>\\s*(\\S+)\\s*<\\w+>|" +     // <id>123<id>
            "\"\\w+\"\\s*:\\s*\"(\\S+)\"\\s*|" +  // "id": "123"
            "\"\\w+\"\\s*:\\s*(\\S+)\\s*|" +      // "id": 123
            "\\b\\w+\\s*->\\s*(\\S+)";            // id -> 123
    private static final Pattern pattern = Pattern.compile(regex);


    public static String fixParameter(String input) {
        Matcher matcher = pattern.matcher(input);

        // 如果找到匹配，返回第一个捕获的参数值
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i);
                }
            }
        }

        // 如果没有匹配，返回 null
        return null;
    }
}
