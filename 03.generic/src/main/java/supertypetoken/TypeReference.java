package supertypetoken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {
    private Type type;

    public TypeReference() {
        Type stype = getClass().getGenericSuperclass();
        if (stype instanceof ParameterizedType) {
            this.type = ((ParameterizedType) stype).getActualTypeArguments()[0];
        } else throw new RuntimeException();
    }

    public Type getType() {
        return type;
    }
}
