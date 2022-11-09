package reference.strong;

import org.junit.Test;
import reference.Team;

import static org.junit.Assert.*;


/**
 * 참조의 기본 개념
 * 실제 객체는 힙 영역에 생성된다.
 * 변수는 힙 영역에 생성된 객체를 참조할 뿐이다.
 * --> 실제 객체와 참조 변수를 분리해서 생각하자, 변수는 객체를 "참조"할 뿐.
 */

public class StrongReferenceTest {

    /**
     * StrongReference: new reference.Team(), t2 = t1 (직접 객체 참조)
     * 대상 객체를 참조하는 StrongReference 참조 변수가 존재하면 객체를 제거하지 않는다. (gc의 대상이 되지 않는다.)
     */

    @Test
    public void strongReference() {
        Team t1 = new Team();
        Team t2 = t1;

        t1 = null;

        // then
        assertNotNull(t2);
    }

}
