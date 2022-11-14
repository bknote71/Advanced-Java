package inference;

import java.util.List;

public class TypeInference {

    public <T> void printParameterType(List<T> list) {
        if (list == null || list.size() == 0) {
            System.out.println("빈 리스트");
            return;
        }

        System.out.println(list.get(0).getClass());
    }

    public void inferenceArgumentType(List<String> list) {

    }

    public <T> void explicitTypeArgument(T t) {
        System.out.println(t.getClass());
    }
}
