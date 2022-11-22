package core.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 어노테이션은 확장이 안된다고 한다...
@Job
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Archer {
}
