package dynamicproxy.aop;

import java.lang.reflect.Method;

public class DefaultPointcut implements Pointcut{
    @Override
    public boolean match(Method method) {
        return true;
    }
}
