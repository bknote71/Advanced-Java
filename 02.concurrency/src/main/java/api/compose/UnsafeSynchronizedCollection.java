package api.compose;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class UnsafeSynchronizedCollection {

    private List<Integer> ints = new Vector<>();

    public void put(Integer integer) {
        ints.add(integer);
    }

    // 동기화된 컬렉션(스레드 안전한 컬렉션)의 연산은 스레드 안전하다.
    // 각각의 연산은 스레드 안전하더라도 연산을 합친 복합 동작/연산은 여전히 불안정할 수 있다.
    // 스레드 안전 클래스가 제공하는 클라이언트 측 락을 사용해서 락을 걸어야함.
    public boolean unsafePutIfAbsent(Integer integer) throws InterruptedException {

        boolean absent = !ints.contains(integer);
        if (absent) {
            Thread.sleep(1000);

            absent = !ints.contains(integer);
            ints.add(integer);
        }
        return absent;
    }

    // 복합 동작
    // 스레드 안전 클래스가 제공하는 클라이언트 측 락을 사용해서 락을 걸어야함.
    // 여기서는 컬렉션 그 자체를 락으로 사용하였다.
    // 이렇게 되면 이 객체에서 "ints" 컬렉션을 사용하는 모든 연산에 락을 걸어야 하는 단점이 생긴다.
    public boolean safePutIfAbsent(Integer integer) throws InterruptedException {
        synchronized (ints) {
            boolean absent = !ints.contains(integer);
            if (absent) {
                Thread.sleep(1000);

                absent = !ints.contains(integer);
                ints.add(integer);
            }
            return absent;
        }
    }

    public void iter() throws InterruptedException {
        for (Integer anInt : ints) {
            System.out.println(anInt);
            Thread.sleep(300);
        }
    }
}
