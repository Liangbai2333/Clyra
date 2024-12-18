package site.liangbai.clyra.annotation;

import site.liangbai.clyra.di.InjectSourceProvider;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectSource {
    Class<? extends InjectSourceProvider>[] value();
}
