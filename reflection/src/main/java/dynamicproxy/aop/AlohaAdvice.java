package dynamicproxy.aop;

import java.lang.reflect.InvocationTargetException;

public class AlohaAdvice implements Advice {

    @Override
    public Object invoke(MethodWrapper method) throws Throwable {
        System.out.println("before method call, hello~");
        final Object invoke = method.invoke();
        System.out.println("after method call, hello~");
        return invoke;
    }
}
