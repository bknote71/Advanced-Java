package core.share.limit;

public class UnSafeLimitedStack {

    // 스레드 한정인 로컬 변수를 외부에 노출시켜서는 안된다.
    // 외부: 로컬을 벗어난 곳
    private OldTree myOldTree;

    public OldTree getMyOldTree() {
        return myOldTree;
    }

    public void unsafeLocalVariable(OldTree oldTree) {
        OldTree localOldTree = new OldTree();

        // task something..
        for (int i = 0; i < 10; ++i) {
            localOldTree.ageIncrement();
        }

        // 매개변수에 로컬 변수를 할당하는 것은 불가능 한 것 같다.
        // 매개변수에 로컬 변수로 새로운 할당하는 것이 애초부터 불가능한가?
        oldTree = localOldTree;
        myOldTree = localOldTree;
    }
}
