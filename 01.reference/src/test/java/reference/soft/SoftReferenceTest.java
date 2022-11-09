package reference.soft;

import org.junit.Test;
import reference.Team;

import java.lang.ref.SoftReference;

import static org.junit.Assert.*;

/**
 *  대상 객체를 참조하는 경우가 SoftReference 객체만 존재하는 경우 GC의 대상이 된다.(즉 제거된다)
 *  단, 정말 메모리가 필요할 때 GC의 대상이 된다.
 *  메모리가 필요할 때? JVM의 메모리가 부족한 경우에만 힙영역에서 제거되고 메모리가 부족하지 않다면 굳이 제거하지 않습니다.
 *
 *  soft reference 는 직접 힙 영역의 객체를 참조하는 것이 아니다.
 *  "soft reference 라는 힙 영역의 객체"가 힙 영역의 있는 객체를 참조하는 것
 *  중요: 대상 객체가 gc의 대상이 되어도 soft reference 객체는 gc의 대상이 아니다. 즉 제거되지 않는다.
 *  대상 객체와, reference 객체는 힙 영역에 생성된 다른 객체이다.
 */
public class SoftReferenceTest {
    @Test
    public void softReference() throws InterruptedException {
        Team t1 = new Team();
        SoftReference<Team> teamSoftReference = new SoftReference<>(t1);

        // softReference의 referent = t1
        assertEquals(t1, teamSoftReference.get());

        t1 = null;

        assertNotNull(teamSoftReference.get());

        System.gc();

        assertNotNull(teamSoftReference.get());

        teamSoftReference.clear();

        assertNotNull(teamSoftReference);
        assertNull(teamSoftReference.get());
    }
}
