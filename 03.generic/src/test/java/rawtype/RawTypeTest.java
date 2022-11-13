package rawtype;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

// raw type을 사용하는 코드가 중간에 들어가게 된다면 타입과 관련하여 심각한 에러가 발생할 수 있다.
public class RawTypeTest {

    @Test
    public void rawTypeList1() {
        List rawList = new ArrayList<Integer>(List.of(1, 2, 3));

        rawList.add(1);

        // rawList.get() 은 Object 이지만, 실제 타입은 Integer 이므로 타입 캐스팅해도 괜찮다.
        final Integer ints = (Integer) rawList.get(0);
        assertEquals(1, ints.intValue());

        // raw type 은 안에 어떤 타입이 들어가도 된다.
        // + 어떤 타입이든 들어갈 수 있지만 특정 타입으로 형변환하려고 하면 당연히 예외가 발생할 수 있다.
        assertThrows(ClassCastException.class, () -> {
            // rawList.get(0) 의 실제 타입은 Integer이다.
            // Integer -> String 형변환은 당연히 실패한다.
            final String s = (String) rawList.get(0);
        });

    }

    @Test
    public void rawTypeList2() {
        final ArrayList<Integer> integers = new ArrayList<>();
        List rawList = integers;

        rawList.add("abc");
        
        // "꺼내 서 변수에 할당할 때" 문제가 발생
        assertThrows(ClassCastException.class, () -> {
            final Integer integer = integers.get(0);
        });
    }

    @Test
    public void rawTypeList3() {
        final List<Integer> ints = Arrays.asList(1, 2, 3);
        List rawList = ints;

        List<String> strs = rawList;
        assertThrows(ClassCastException.class, () -> {
            final String s = strs.get(0);
        });
    }

}