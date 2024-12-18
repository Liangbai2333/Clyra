package site.liangbai.clyra.utils;

import org.junit.jupiter.api.Test;

public class TypeUtilsTest {
    @Test
    public void testConvertToPrimitiveType() {
        String value = "123";
        Integer result1 = TypeUtils.convertToPrimitiveType(Integer.class, value);
        System.out.println(result1);
        int result2 = TypeUtils.convertToPrimitiveType(int.class, value);
        System.out.println(result2);
        Double result3 = TypeUtils.convertToPrimitiveType(Double.class, value);
        System.out.println(result3);
        String result4 = TypeUtils.convertToPrimitiveType(String.class, value);
        System.out.println(result4);
        boolean result5 = TypeUtils.convertToPrimitiveType(boolean.class, "true");
        System.out.println(result5);
    }
}
