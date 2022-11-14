package core.share.immutable.perfect;

public class VolatileCachedFactorizer {

    private volatile OneValueCache cache = new OneValueCache(null, null);

    public void service() {
        Integer i = 100;
        Integer[] factors = cache.getFactors(i);
    }
}
