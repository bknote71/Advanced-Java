package typesafe;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HeterogeneousContainer {

    private Map<Class<?>, Object> map = new HashMap<>();

    public HeterogeneousContainer() {}

    public <T> void put1(Class<T> clazz, T o) {
        this.map.put(Objects.requireNonNull(clazz), o);
    }

    public <T> void put2(Class<T> clazz, T o) {
        this.map.put(Objects.requireNonNull(clazz), clazz.cast(o));
    }


    public <T> T get(Class<T> clazz) {
        return clazz.cast(this.map.get(clazz));
    }
}
