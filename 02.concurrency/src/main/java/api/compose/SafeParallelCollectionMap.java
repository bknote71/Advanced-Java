package api.compose;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SafeParallelCollectionMap {

    private Map<Class<?>, Object> map = new ConcurrentHashMap<>();

    public <T> void put(Class<T> clazz, T object) {
        map.put(clazz, object);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(map.get(clazz));
    }

    public <T> boolean putIfAbsent(Class<T> clazz, T object) {
        return map.putIfAbsent(clazz, object) == null;
    }

    // 미약한 일관성 전략
    //👉🏻 반복문 중 컬렉션 내용 변경해도 Iterator 만든 시점 상황대로 반복 계속 가능
    public void iter() throws InterruptedException {
        for (Map.Entry<Class<?>, Object> entry : map.entrySet()) {
            final Class<?> key = entry.getKey();
            final Object value = entry.getValue();
            Thread.sleep(300);
            System.out.println("key: " + key + ", " + "Object: " + value);
        }
    }

    public int size() {
        return map.size();
    }
}
