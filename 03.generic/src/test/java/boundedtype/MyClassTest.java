package boundedtype;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyClassTest {

    @Test
    public void type_safety() {
        MyClass<Integer> integerMyClass = new MyClass<>(Integer.valueOf(123));

        // String은 최상위 타입인 Number 를 상속하지 않기 때문에 컴파일 에러가 발생한다.
        // MyClass<String> stringMyClass = new MyClass<>();

        integerMyClass.printType();
    }

    @Test
    public void type_compatibility() {
        // 호환성 때문에 안된다.
        // MyClass<Number> myClass = new MyClass<Integer>();

        MySubClass<Number> numberMySubClass = new MySubClass<>();
        MyClass<Number> myClass = numberMySubClass;
    }

}