package lambda;

import javax.swing.*;
import java.util.function.Function;

public class ShadowingVariable {

    /**
     * 람다, 익명/로컬 내부 클래스의 공통점:
     * - 로컬변수 사용 가능
     * - 로컬변수 사용 시 멱등성을 보장하고, 멤버변수 사용 시 멱등성을 보장하지 못한다.
     * <p>
     * 차이점은? 쉐도잉: 내부 블록의 변수가 외부 블록의 변수를 가린다.
     * - 우선순위의 개념
     * - 람다는 쉐도잉을 적용할 수 없다!!!!!!!!!!!!
     * - 익명/로컬 내부 클래스는 쉐도잉 적용 가능
     */
    public int plus10baseNumberByLambda(Integer integer) {
        int baseNumber = 10;

        // target type: Function<Integer, Integer>
        Function<Integer, Integer> func = i -> {
            // 람다는 로컬 변수를 쉐도잉 할 수 없다. (매개변수도 로컬변수 취급)
            // int baseNumber = 11;
            // Integer integer = 11;
            return i + baseNumber;
        };

        return func.apply(integer);
    }

    public int plus10baseNumberByLocal(Integer integer) {
        int baseNumber = 10;
        class LocalClass {
            int baseNumber = 11;
            int integer = 12;

            public int plus() {
                return baseNumber + integer;
            }
        }

        return new LocalClass().plus();
    }
}
