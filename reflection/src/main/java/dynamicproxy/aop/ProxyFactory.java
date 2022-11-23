package dynamicproxy.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;

public class ProxyFactory {
    Object target;
    Class<?> targetClass;
    Class<?> parent;

    Advice advice;
    Pointcut pointcut = new DefaultPointcut();

    public ProxyFactory(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.parent = targetClass.getSuperclass();
    }

    public ProxyFactory(Object target) {
        this.targetClass = target.getClass();
        this.target = target;
    }

    public void addAdvice(Advice advice) {
        this.advice = advice;
    }

    public void addPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public Object advisedMethodCall(MethodWrapper wrapper) throws Throwable {
        return advice.invoke(wrapper);
    }

    public Object getProxy() {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass); // 상속하고 호출할 Target Class
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if(pointcut != null && pointcut.match(method))
                return advisedMethodCall(new MethodWrapper(obj, method, args, proxy));
            return proxy.invokeSuper(obj, args); // 꼭 real object가 필요한 것이 아니라 이런식으로도 메서드 호출이 가능하구나....
        });
        return enhancer.create();
    }

}
