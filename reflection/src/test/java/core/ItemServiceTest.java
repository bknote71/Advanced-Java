package core;

import core.item.Bow;
import core.item.Item;
import core.item.Sword;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * reflection:
 * - 동적으로(런타임에) 클래스를 조작할 수 있다.
 * - "클래스 정보"를 기반으로 클래스를 생성/필드 접근/필드 설정/메소드 실행
 * - 클래스 정보(Class<T> 정보) -> 메모리 메소드 영역에 존재
 *
 * 중요:
 * - annotation.getClass()는 proxy 객체이다.
 * - 따라서 구하고자하는 이름, value()값을 구할 수 없다.
 * - annotation의 구하고자하는 이름, value값을 구하려면 annotationType() 을 이용해야 한다. (애노테이션에 붙은 어노테이션을 구할 때도 필요)
 * 
 * 예) 어노테이션의 이름 구하기
 * type = annotation.annotationType();
 * type.getSimpleName();
 *
 * 예) 어노테이션의 value() 구하기
 * - value()는 public 메소드 취급을 받는다. + no argument
 * type = annotation.annotationType();
 * Method method = type.getMethod("value", null) or type.getDeclaredMethod("value", null)
 * - public method이기 때문에 둘 다 가능
 * value = method.invoke(annotation, null); // invoke 대상은 "annotation" 이다.
 */
public class ItemServiceTest {

    final ItemService itemService = new ItemService();

    @Test
    public void produce() {
        final Item item = itemService.produceCommonItem("몽둥이");
        assertNotNull(item);
    }

    @Test
    public void getItemId() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        final Item item = itemService.produceCommonItem("몽둥이");
        final Long itemId = itemService.getItemId(item);
        Long expected = 0L;
        assertEquals(expected, itemId);
    }

    @Test
    public void strengthen() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Item item = itemService.produceCommonItem("몽둥이");

        // 7번 강화함...
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        item = itemService.strengthen(item);
        System.out.println(item.getStarforce());
    }

    @Test
    public void attack() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Item bow = new Bow("애쉬활", 5);
        itemService.attack(bow);
    }

    @Test
    public void findJob() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Item item = new Sword("낡은검", 1);

        final Annotation annotation = itemService.findJobsThatUseThis(item);

        // 중요: annotation.getClass()는 proxy 객체이다. 따라서 구하고자하는 이름, value()값을 구할 수 없다.
        // annotation의 구하고자하는 이름, value값을 구하려면 annotationType() 을 이용해야 한다. (애노테이션에 붙은 어노테이션을 구할 때도 필요)
        final Class<? extends Annotation> annotationClass = annotation.getClass();
        assertTrue(Proxy.isProxyClass(annotationClass));

        final Class<? extends Annotation> annotationType = annotation.annotationType();
        System.out.println(annotationType);
        assertEquals("Warrior", annotationType.getSimpleName());

        final Method value = annotationType.getDeclaredMethod("value", null);
        assertEquals(1, value.invoke(annotation, null));
    }
}