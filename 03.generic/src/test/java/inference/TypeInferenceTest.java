package inference;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TypeInferenceTest {

    @Test
    public void inferenceParameterType() {
        List<String> list = List.of("1", "abc");

        final TypeInference typeInference = new TypeInference();
        typeInference.printParameterType(list);
    }

    // 앞에 선언된 변수의 타입 정보를 가지고 타입 추론을 한다.
    @Test
    public void inferenceInstanceType() {
        List<String> list = new ArrayList<>();
        List<String> strings = Collections.emptyList();
    }

    @Test
    public void inferenceArgumentType() {
        final TypeInference typeInference = new TypeInference();
        typeInference.inferenceArgumentType(Collections.emptyList());
    }

    // 타입 추론을 할 수 없는 경우 명시적으로 타입을 선언해야 한다.
    @Test
    public void typeWitness() {
        // explicit type argument
        final TypeInference typeInference = new TypeInference();

        // T 타입 파라미터를 Number로 명시적으로 선언해줬다.
        // 따라서 Number와 Number의 하위타입이 아닌 타입을 인자로 넘길 수 없다.
        // typeInference.<Number>explicitTypeArgument("abc");

        typeInference.<Number>explicitTypeArgument(123.1);
    }

}