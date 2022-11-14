package supertypetoken;

import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.junit.Assert.*;

public class TypeReferenceTest {

    @Test
    public void type_parameter_checking() {
        final TypeReference<String> typeReference = new TypeReference<>() {};

        System.out.println(typeReference.getType());
    }

    @Test
    public void generic_type_token() {
        // List<String>.class 는 컴파일적으로 불가능하다.
        final TypeReference<List<String>> listTypeReference = new TypeReference<List<String>>(){};

        System.out.println(listTypeReference.getType());
    }

}