package core;

import core.item.Item;
import core.job.Job;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ItemService {

    /**
     * reflection의 시작은 Class<?>
     * Class<?>에 접근하는 방법
     * 1. 타입.class
     * 2. 인스턴스.getClass()
     * 3. Class.forName("FQCN")
     * - FQCN: 풀패키지경로 + (클래스로더)
     * - classpath에 해당하는 클래스가 없다면 ClassNotFoundException 발생
     *
     */
    public Item produceCommonItem(String name) {
        Class<Item> itemClass = Item.class;
        try {
            final Constructor<Item> constructor = itemClass.getConstructor(String.class);
            final Item item = constructor.newInstance(name);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getItemId(Item item) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final Class<?> itemClass = Class.forName("core.item.Item");
        final Field id = itemClass.getDeclaredField("id");

        id.setAccessible(true); // id field는 private이므로 setAccessible을 true로 설정해야 접근이 가능하다.
        return (Long) id.get(item);
    }

    public Item strengthen(Item item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<? extends Item> itemClass = item.getClass();
        final Method strengthen = itemClass.getMethod("strengthen", int.class);
        strengthen.invoke(item, item.getStarforce());
        return item;
    }

    public void attack(Item item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<? extends Item> itemClass = item.getClass();
        final Method attack = itemClass.getMethod("attack", null);
        attack.invoke(item, null);
    }

    public Annotation findJobsThatUseThis(Item item) {
        final Class<? extends Item> itemClass = item.getClass();
        final Annotation[] annotations = itemClass.getDeclaredAnnotations();
        // 어노테이션에 붙은 어노테이션: annotationType
        // annotationType: Returns the annotation type of this annotation
        // 참고로 어노테이션은 확장이 안된다.
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getAnnotation(Job.class) != null) {
                return annotation;
            }
        }
        return null;
    }
}
