package dynamicproxy.aop;

import dynamicproxy.Target;
import dynamicproxy.TargetImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProxyFactoryTest {

    @Test
    public void getProxyTest() {
        final ProxyFactory proxyFactory = new ProxyFactory(new TargetImpl());
        proxyFactory.addAdvice(new AlohaAdvice());
        final Target proxy = (Target) proxyFactory.getProxy();
        proxy.method();
    }

}