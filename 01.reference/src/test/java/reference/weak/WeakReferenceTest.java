package reference.weak;

import org.junit.Test;
import reference.Team;

import java.lang.ref.WeakReference;

import static org.junit.Assert.*;

/**
 * 대상 객체를 참조하는 경우가 WeakReference 객체만 존재하는 경우 그 객체는 GC의 대상이 된다.
 * GC 실행 시 무조건 힙 메모리에서 제거된다.
 * WeakHashMap 에서 사용: 맵의 엔트리를 맵의 Value가 아니라 Key에 의존해야 하는 경우에 사용할 수 있다.
 * WeakHashMap 은 내부적으로 weak reference 참조 객체 자체를 정리해주는 로직이 들어가 있다.
 * weak reference를 사용할 때 대상 객체는 정리해줘도 weak reference 자체는 없어지지 않기 때문에 사용하는데 주의가 필요하다.
 *
 * ex) 캐시를 구현하는데 사용할 수 있지만, 캐시를 직접 구현하는 것은 권장하지 않는다.
 * (톰캣 컨테이너의 ConcurrentCache class에서 WeakHashMap을 사용 중)
 *
 * 참고: weak reference 는 직접 힙 영역의 객체를 참조하는 것이 아니다.
 * weak reference 라는 힙 영역의 객체가 힙 영역의 있는 객체를 참조하는 것
 * 중요: 대상 객체가 gc의 대상이 되어도 weak reference 객체는 gc의 대상이 아니다. 즉 제거되지 않는다.
 * 대상 객체와, reference 객체는 힙 영역에 생성된 다른 객체이다.
 */
public class WeakReferenceTest {

    @Test
    public void weakReference() {
        Team team = new Team();
        WeakReference<Team> reference = new WeakReference<>(team);

        team = null;

        System.gc();

        assertNull(reference.get());
    }
}
