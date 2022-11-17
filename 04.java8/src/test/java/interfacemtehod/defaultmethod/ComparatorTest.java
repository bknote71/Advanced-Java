package interfacemtehod.defaultmethod;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ComparatorTest {

    @Test
    public void test() {
        List<Integer> list = new ArrayList<>(List.of(3, 4, 7, 9, 2, 1, 5, 6, 8, 10));

        // 양수면 순서 변경, 음수면 정상
        // 지금은 오름차순
        Comparator<Integer> cmp = (o1, o2) -> o1 - o2;
        list.sort(cmp);

        System.out.println(list);

        System.out.println("reversed");
        list.sort(cmp.reversed());
        System.out.println(list);

        System.out.println("then comparing");
        // thenComparing(Comparator): 첫번째 Comparator의 compare 메서드에서 두 값이 일치하면 실행하는 다음 비교 함수
        list.sort(cmp.thenComparing((o1, o2) -> o2 - o1));
        System.out.println(list);
    }
}
