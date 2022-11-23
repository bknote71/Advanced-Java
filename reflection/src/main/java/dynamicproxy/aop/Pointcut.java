package dynamicproxy.aop;

import java.lang.reflect.Method;

public interface Pointcut {
    boolean match(Method method);
}
