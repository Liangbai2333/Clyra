package site.liangbai.clyra.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandHandler {
    String value() default "";

    String description() default "";
}
