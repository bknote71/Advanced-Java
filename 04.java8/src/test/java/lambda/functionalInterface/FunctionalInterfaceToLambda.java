package lambda.functionalInterface;

import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionalInterfaceToLambda {

    /**
     * FunctionalInterface?
     * - abstract method가 오직 1개인 interface
     * - 익명 내부 클래스로 구현
     * ==> 람다로 표현/변환 가능
     *
     * Target type of a lambda conversion must be an interface
     * Target type: 선언된 변수, 매개변수 타입
     */


    @Test
    public void functionalInterface() {
        Animal animal = new Animal() {
            @Override
            public void cry() {
                System.out.println("월월");
            }
        };

        animal.cry();
    }

    @Test
    public void anonymousAbstract() {
        AbstractAnimal animal = new AbstractAnimal() {
            @Override
            public void cry() {
                System.out.println("왈왈");
            }
        };

        animal.cry();
    }

    @Test
    public void functionalInterfaceToLambda() {
        // 람다는 오직 FunctionalInterface 를 구현한다. (변환)
        Animal animal = () -> System.out.println("왈왈");

        animal.cry();
    }

    @Test
    public void abstractClassToLambda() {
        // Target type of a lambda conversion must be an interface
        // 람다 변환의 대상 유형은 인터페이스여야 합니다.
        // target type: 선언된 변수, 매개변수 타입, ..
        // AbstractAnimal animal = () -> System.out.println("왈왈");
    }


}