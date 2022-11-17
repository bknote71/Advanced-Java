package interfacemtehod.defaultmethod;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultMethodTest {

    @Test
    public void defaultMethod() {
        final Cat cat = new Cat();
        final Dog dog = new Dog();

        cat.isCute();
        dog.isCute();

        cat.isIntelligentBetterThanPeople();
        dog.isIntelligentBetterThanPeople();

        cat.match(dog);
    }

    // 해당 인터페이스를 구현한 클래스를 깨트리지 않고 새 기능을 추가할 수 있다.
    // default method는 구현하는 클래스 입장에서 알지 못한다.
    // 기본 메소드는 구현체가 모르게 추가된 기능으로 그만큼 리스크가 있다.
    // 컴파일 에러는 아니지만 구현체에 따라 런타임 에러가 발생할 수 있다.
    @Test
    public void stealth() {
        final Lion lion = new Lion();
        assertThrows(NullPointerException.class, () -> {
            lion.nameIs("사자");
        });
    }

}