package dynamicproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TargetImpl implements Target {
    @Override
    public void method() {
        System.out.println("targetImpl method call");
    }

    public static void main(String[] args) {
        // JDK dynamic proxy는 반드시 인터페이스를 대상으로 해야한다.
        // 무조건 인터페이스!! 구현체 클래스 기반으로 프록시를 못만든다.
        // 프록시를 감싸는 프록시를? 못만든다. 프록시는 구현체 클래스이기 때문에
        // 클래스 기반: cglib, byteBuddy
        System.out.println("==========jdk==========");
        final Target jdkDynamicProxy = (Target) Proxy.newProxyInstance(Target.class.getClassLoader(), new Class[]{Target.class},
                new InvocationHandler() {
                    Target target = new TargetImpl();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("before method call");
                        final Object invoke = method.invoke(target, args);
                        System.out.println("after method call");
                        return invoke;
                    }
                });

        System.out.println(Proxy.isProxyClass(jdkDynamicProxy.getClass()));
        jdkDynamicProxy.method();
        System.out.println("==========Cglib==========");
        // cglib: Enhancer + invocationHandler(methodInterceptor, advisor)
        // cglib는 런타임에 바이트코드를 조작한다 (런타임 위빙)
        // 상속을 사용하지 못하는 경우 프록시를 만들 수 없다. (private 생성자, final 클래스)
        final MethodInterceptor handler = new MethodInterceptor() {
            TargetImpl target = new TargetImpl();

            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before method call by cg proxy");
                final Object invoke = method.invoke(target, args);
                System.out.println("after method call by cg proxy");
                return invoke;
            }
        };

        final TargetImpl target = (TargetImpl) Enhancer.create(TargetImpl.class, handler);
        System.out.println(target); // target.toString()이 호출되기 때문에 역시 proxy method 처리를 받는다.
        target.method();

        // 위의 여러 문제들을 제외하고 추가적인 핵심 문제
        // 1. 대상 지정 문제: 일일히 if문으로 비교하면서 프록시를 적용할 메소드를 찾아야 하는 문제
        // 2. handler(advice): 프록시를 설정할 때마다 구현해야 한다.
        // --> 스프링 AOP (프록시 팩토리): (Pointcut + advice) = Advisor 지원
    }
}
