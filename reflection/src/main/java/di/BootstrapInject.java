package di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BootstrapInject {

    Container container = new Container();

    public void inject(String packageName) {
        // 패키지 탐색, package 안에서 주입할 놈들 주입 + 패키지안에 패키지는 어떻게할까???
        System.out.println("===================================================");
    }

    public <T> T createInstance(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getDeclaredAnnotation(Inject.class) != null) {
                // 의존성 주입, cycle 가정 X
                List<Object> obs = new ArrayList<>();
                final Parameter[] parameters = constructor.getParameters();
                for (Parameter parameter : parameters) {
                    Object o = container.getObject(parameter.getType());
                    if (o == null)
                        o = createInstance(parameter.getType());
                    obs.add(o);
                }
                return (T) constructor.newInstance(obs.toArray());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        final BootstrapInject bootstrapInject = new BootstrapInject();
        bootstrapInject.inject("di");
    }
}
