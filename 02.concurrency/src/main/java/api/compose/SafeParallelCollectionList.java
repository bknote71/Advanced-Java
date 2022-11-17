package api.compose;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SafeParallelCollectionList {
    // 읽고 쓰기에 안전한 병렬 컬렉션
    private List<Integer> list = new CopyOnWriteArrayList<>();

    public void add(Integer integer) {
        this.list.add(integer);
    }

    public void get(int idx) {
        this.list.get(idx);
    }

    public boolean putIfAbsent(Integer integer) throws InterruptedException {
        // CopyOnWriteArrayList는 putIfAbsent 연산은 지원하지 않는 듯 하다.
        // 따라서 putIfAbsent 는 스레드 불안정이다.
        boolean absent = !list.contains(integer);
        if (absent) {
            Thread.sleep(1000);
            absent = !list.contains(integer);
            list.add(integer);
        }
        return absent;
    }

    // 미약한 일관성 전략
    //👉🏻 반복문 중 컬렉션 내용 변경해도 Iterator 만든 시점 상황대로 반복 계속 가능
    public void iter() throws InterruptedException {
        for (Integer anInt : list) {
            System.out.println(anInt);
            Thread.sleep(300);
        }
    }

    public int size() {
        return list.size();
    }
}
