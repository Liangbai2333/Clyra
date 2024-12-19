package site.liangbai.clyra.utils;

import org.junit.jupiter.api.Test;

public class RegexUtilsTest {
    @Test
    public void test() {
        String rawInput = "id=123";
        System.out.println(RegexUtils.fixParameter(rawInput));
    }
}
