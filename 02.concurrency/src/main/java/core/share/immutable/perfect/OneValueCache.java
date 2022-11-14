package core.share.immutable.perfect;

import java.util.Arrays;

public class OneValueCache {

    private final Integer lastNumber;
    private final Integer[] lastFactors;

    public OneValueCache(Integer integer, Integer[] factors) {
        lastNumber = integer;
        lastFactors = Arrays.copyOf(factors, factors.length);
    }

    public Integer[] getFactors(Integer integer) {
        if (lastNumber == null || !lastNumber.equals(integer))
            return null;
        else
            return Arrays.copyOf(lastFactors, lastFactors.length);
    }
}
