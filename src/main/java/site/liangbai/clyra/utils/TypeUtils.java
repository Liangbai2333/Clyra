package site.liangbai.clyra.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeUtils {
    // 基础类型与包装类的映射
    private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new ConcurrentHashMap<>();

    static {
        primitiveToWrapperMap.put(boolean.class, Boolean.class);
        primitiveToWrapperMap.put(byte.class, Byte.class);
        primitiveToWrapperMap.put(char.class, Character.class);
        primitiveToWrapperMap.put(double.class, Double.class);
        primitiveToWrapperMap.put(float.class, Float.class);
        primitiveToWrapperMap.put(int.class, Integer.class);
        primitiveToWrapperMap.put(long.class, Long.class);
        primitiveToWrapperMap.put(short.class, Short.class);
        primitiveToWrapperMap.put(void.class, Void.class);  // 特殊情况，`void` 类型
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        // 判断是否为基础类型
        if (clazz.isPrimitive()) {
            return true;
        }

        // 判断是否为包装类（包括Byte、Short、Integer、Long、Float、Double、Character、Boolean）
        return clazz == Byte.class || clazz == Short.class || clazz == Integer.class ||
                clazz == Long.class || clazz == Float.class || clazz == Double.class ||
                clazz == Character.class || clazz == Boolean.class;
    }

    /**
     * 获取基础类型对应的包装类
     * @param type 基础类型
     * @return 对应的包装类，若是包装类直接返回原类型
     */
    public static Class<?> wrap(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type cannot be null");
        }

        // 如果是基础类型，返回对应的包装类
        if (type.isPrimitive()) {
            return primitiveToWrapperMap.get(type);
        }

        // 如果已经是包装类，直接返回本身
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertToPrimitiveType(Class<T> type, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        // 处理String类型
        if (type == String.class) {
            return type.cast(value);  // 直接返回字符串
        }

        // 处理其他基础类型或包装类
        if (type == Integer.class) {
            return type.cast(Integer.valueOf(value));
        } else if (type == int.class) {
            return (T) Integer.valueOf(value);  // 解析为int类型
        } else if (type == Double.class) {
            return type.cast(Double.valueOf(value));
        } else if (type == double.class) {
            return (T) Double.valueOf(value);  // 解析为double类型
        } else if (type == Float.class) {
            return type.cast(Float.valueOf(value));
        } else if (type == float.class) {
            return (T) Float.valueOf(value);  // 解析为float类型
        } else if (type == Long.class) {
            return type.cast(Long.valueOf(value));
        } else if (type == long.class) {
            return (T) Long.valueOf(value);  // 解析为long类型
        } else if (type == Short.class) {
            return type.cast(Short.valueOf(value));
        } else if (type == short.class) {
            return (T) Short.valueOf(value);  // 解析为short类型
        } else if (type == Byte.class) {
            return type.cast(Byte.valueOf(value));
        } else if (type == byte.class) {
            return (T) Byte.valueOf(value);  // 解析为byte类型
        } else if (type == Boolean.class) {
            return type.cast(Boolean.valueOf(value));
        } else if (type == boolean.class) {
            return (T) Boolean.valueOf(value);  // 解析为boolean类型
        } else if (type == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("String must have exactly one character for char type");
            }
            return (T) Character.valueOf(value.charAt(0));  // 解析为char类型
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
    }
}
