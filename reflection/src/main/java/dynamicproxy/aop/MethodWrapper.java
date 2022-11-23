package dynamicproxy.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodWrapper {
    Object obj;
    Method method;
    Object[] args;
    MethodProxy proxy;

    public MethodWrapper(Object obj, Method method, Object[] args, MethodProxy proxy) {
        this.obj = obj;
        this.method = method;
        this.args = args;
        this.proxy = proxy;
    }

    public Object invoke() throws Throwable {
        return proxy.invokeSuper(obj, args);
    }
}
