package lambda.methodReference;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

import static org.junit.Assert.*;

public class MethodReferenceTest {

    // 특정 객체(instance)의 인스턴스 메서드 참조
    @Test
    public void instanceMethodReference() {
        final Greeting greeting = new Greeting();

        Consumer<Integer> con = i -> greeting.setValue(i);
        Supplier<Integer> sup = () -> greeting.getValue();

        con.accept(10);
        assertEquals(Integer.valueOf(10), sup.get());

        //--> method reference 적용
        IntConsumer con2 = greeting::setValue;
        IntSupplier sup2 = greeting::getValue;

        con2.accept(30);
        assertEquals(30, sup2.getAsInt());
    }
    
    // 스태틱 메서드 참조
    @Test
    public void staticMethodReference() {
        Consumer<Integer> con = i -> Greeting.setStaticValue(i);
        Supplier<Integer> sup = () -> Greeting.getStaticValue();

        con.accept(10);
        assertEquals(Integer.valueOf(10), sup.get());

        //--> method reference 적용
        IntConsumer con2 = Greeting::setStaticValue;
        IntSupplier sup2 = Greeting::getStaticValue;

        con2.accept(30);
        assertEquals(30, sup2.getAsInt());
    }

    // 생성자 참조
    @Test
    public void constructorTest() {
        Supplier<Greeting> sup = Greeting::new;
        Function<Integer, Greeting> func = Greeting::new;

        // 둘이 다른 생성자를 호출한다.
        // 생성자의 경우 단순하게 메서드레퍼런스만을 보고 어떤 생성자가 사용되는지 알기 힘들기 때문에
        // 함수형인터페이스의 선언 부분을 잘 봐야한다.
    }

    // 임의의 객체의 인스턴스 메서드 대한 참조
    @Test
    public void instanceMethodReferenceByAll() {
        Greeting[] greetings = {new Greeting(1), new Greeting(2), new Greeting(3)};

        // target type: Comparator<Greeting>
        // 람다, 메서드 래퍼런스는 target type 인 Comparator를 표현하기는 하지만 Comparator에 대한 선언이 없기 때문에
        // Comparator의 메서드를 사용할 수 없다.
        // Comparator에 대한 정보가 없다 --> 활용할 수 없다.
        Arrays.sort(greetings, Greeting::myComp);

        assertEquals(3, greetings[0].value);
        assertEquals(2, greetings[1].value);
        assertEquals(1, greetings[2].value);

        // 람다로 Comparator를 표현한 것을 명시적으로 선언해줬다.
        // Comparator에 대한 정보를 확인할 수 있다.
        Comparator<Greeting> greetingComparator = (g1, g2) -> g1.myComp(g2);
        Arrays.sort(greetings, greetingComparator.reversed().reversed());

        assertEquals(3, greetings[0].value);
        assertEquals(2, greetings[1].value);
        assertEquals(1, greetings[2].value);


        Arrays.sort(greetings, new Comparator<Greeting>() {
            @Override
            public int compare(Greeting o1, Greeting o2) {
                return o2.value - o1.value;
            }
        }.reversed());

        System.out.println(Arrays.toString(greetings));
    }
}