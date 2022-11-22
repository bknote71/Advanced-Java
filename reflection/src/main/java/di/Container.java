package di;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Container {

    private Map<Class<?>, Object> map = new HashMap<>();

    public <T> void push(Class<T> clazz, T Object) {
        this.map.put(Objects.requireNonNull(clazz), clazz.cast(Object));
    }

    public <T> T getObject(Class<T> clazz) {
        return (T) this.map.get(clazz);
    }

    @Override
    public String toString() {
        return "Container{" +
                "map=" + map +
                '}';
    }
}
