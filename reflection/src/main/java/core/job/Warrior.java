package core.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Job
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Warrior {
    int value() default 1;
}
