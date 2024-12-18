package site.liangbai.clyra.utils;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {
    @Test
    public void testRemovePrefix() {
        String str = "hello world";
        String prefix = "hello";
        String result = StringUtils.removePrefix(str, prefix);
        System.out.println(StringUtils.trimStart(result));
    }
}
