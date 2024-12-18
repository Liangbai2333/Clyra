package site.liangbai.clyra.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandParam {
    String value() default "";

    String description() default "";
}
