package di;

import di.annotation.Component;
import di.annotation.Inject;
import di.repository.BookRepository;
import di.service.BookService;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BootstrapInject {

    Container container = new Container();

    public void inject(String packageName) throws URISyntaxException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 패키지 탐색, package 안에서 주입할 놈들 주입 + 패키지안에 패키지는 어떻게할까???
        // package explorer 추가
        final Class[] classesInPackage = PackageExplorer.getClassesInPackage(packageName);
        // 의존성 중에 사이클 없다고 가정!
        Arrays.stream(classesInPackage)
                .filter(BootstrapInject::hasComponent)
                .filter(Predicate.not(Class::isAnnotation))
                .forEach(c->{
                    try {
                        container.push(c, c.cast(createInstance(c)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    // @Component가 붙은 클래스를 생성하도록 한다.
    // @Component가 없으면 컨테이너가 관리하지 못한다. 즉 의존성 주입도 못한다.
    public <T> T createInstance(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchBeanException, NoSuchMethodException {
        final Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getDeclaredAnnotation(Inject.class) != null) {
                // 의존성 주입, cycle 가정 X
                List<Object> obs = new ArrayList<>();
                final Parameter[] parameters = constructor.getParameters();
                for (Parameter parameter : parameters) {
                    final Class<?> type = parameter.getType();
                    if(!hasComponent(type)) throw new NoSuchBeanException();
                    Object o = container.getObject(type);
                    if (o == null) {
                        o = createInstance(parameter.getType());
                    }
                    obs.add(o);
                }
                return (T) constructor.newInstance(obs.toArray());
            }
        }
        // Inject는 없어서 의존성 주입을 못했다.
        // 없으면 2가지 경우:
        // 1. 생성자가 1개인 경우: 의존성 주입
        // 2. error ?
        // 여기서는 기본 생성자로만 생성하도록 한다.
        final Constructor<T> constructor = clazz.getConstructor(null);
        return (T) constructor.newInstance(null);
    }

    private static boolean hasComponent(Class<?> clazz) {
        // 재귀적으로 찾는다.
        boolean flag = false;
        final Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> type = annotation.annotationType();
            // retention, target 무시
            if(type == Retention.class || type == Target.class) continue;
            if (type == Component.class) return true;
            // 재귀적으로 서치
            if (hasComponent(type)) return true;
        }
        return false;
    }

    public static void main(String[] args) throws URISyntaxException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchBeanException, NoSuchMethodException {
        final BootstrapInject bootstrapInject = new BootstrapInject();
        bootstrapInject.inject("di");
        System.out.println(bootstrapInject.container);
    }
}
