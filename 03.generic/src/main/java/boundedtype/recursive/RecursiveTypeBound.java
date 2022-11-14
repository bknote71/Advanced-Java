package boundedtype.recursive;

import java.util.List;

public class RecursiveTypeBound {

    // Comparator와 Comparable은 소비자
    //
    public static <E extends Comparable<? super E>> E max(List<? extends E> list) {
        if (list.isEmpty())
            throw new IllegalArgumentException("빈 리스트");

        E result = null;
        for (E e : list) {
            if (result == null || e.compareTo(result) > 0)
                result = e;
        }

        return result;
    }

}
