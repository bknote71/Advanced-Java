package core.share.limit;

// stack = thread limit
// stack = local variable, info of method call, ..
public class SafeLimitedStack {

    public static boolean notSafe = false;

    public void localVariable() throws Exception {
        OldTree oldTree = new OldTree();
        for (int i = 0; i < 10000; ++i) {
            oldTree.ageIncrement();
        }

        if (oldTree.getAge() != 10000) {
            throw new Exception("스레드 불안정");
        }
    }
}
