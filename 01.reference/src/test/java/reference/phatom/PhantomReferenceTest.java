package reference.phatom;

import org.junit.Test;
import reference.Team;
import reference.phantom.TeamReference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import static org.junit.Assert.*;


/**
 * strong reference가 없고 phantom reference만 있으면
 * gc가 일어날 때 객체의 메모리는 정리하고 팬텀 레퍼런스는 레퍼런스 큐에 넣어주고 나중에 정리한다.
 * 기본적으로 ReferenceQueue 가 있어야 한다.
 *
 * 사용 처) 자원 정리, 언제 객체의 메모리가 해제되는지 알 수 있다.
 * --> 최종적으로 레퍼런스 큐에서 "팬턴_레퍼런스_객체"를 꺼내서 정리할 수 있다. (즉 원본 객체 삭제 후의 후처리가 가능하다?)
 * --> 즉, 여러 객체 중에서 원본 객체가 null이 된 객체의 래퍼런스만을 뽑아서(enqueue 된 객체) 후처리가 가능하다.
 *
 * 중요: 원본 객체가 null인 객체의 "레퍼런스 객체"만을 큐에 담아서 따로 후처리가 가능해진다!!
 * ex) 래퍼런스 객체의 정리
 */
public class PhantomReferenceTest {

    /**
     * 대상 객체(모든 strong reference)에 null을 할당: Phantom reachable 상태(phantom)
     * GC에 의해 phantomReference의 참조값이 메모리에서 제거된 후(clear(): referent = null), referenceQueue에 삽입됩니다. (p)
     * referenceQueue.enqueue() 호출
     */
    @Test
    public void referenceGet() {
        Team team = new Team();

        // 참조 객체를 정리해주는 reference queue
        ReferenceQueue<Team> rq = new ReferenceQueue<>();
        PhantomReference<Team> phantom = new PhantomReference<>(team, rq);

        // phantom reference get 은 항상 null을 반환
        assertNull(phantom.get());
    }

    @Test
    public void allocationNull() throws InterruptedException {
        Team team = new Team();

        ReferenceQueue<Team> rq = new ReferenceQueue<>();
        PhantomReference<Team> phantom = new PhantomReference<>(team, rq);

        team = null;

        System.out.println("before gc, phantom reachable? : " + phantom.isEnqueued());
        System.gc();

        // 팬텀_레퍼런스를 큐에 집어 넣는데 조금의 시간이 필요하다.
        Thread.sleep(1000);
        System.out.println("after gc, phantom reachable? : " + phantom.isEnqueued());

        final Reference<? extends Team> poll = rq.poll();

        assertEquals(phantom, poll);

        // reference 를 정리할 수 있다.
        poll.clear();
    }

    // 중요: 원본 객체가 null인 객체만을 큐에 담아서 따로 후처리가 가능해진다!!
    @Test
    public void postProcessReference() throws InterruptedException {
        Team team1 = new Team();
        Team team2 = new Team();
        Team team3 = new Team();
        Team team4 = new Team();

        ReferenceQueue<Team> rq = new ReferenceQueue<>();
        TeamReference phantom1 = new TeamReference(team1, rq);
        TeamReference phantom2 = new TeamReference(team2, rq);
        TeamReference phantom3 = new TeamReference(team3, rq);
        TeamReference phantom4 = new TeamReference(team4, rq);

        team1 = null;
        team3 = null;

        System.gc();

        // enqueue 하는데 시간이 걸린다..
        Thread.sleep(1000);

        assertTrue(phantom1.isEnqueued());
        assertTrue(phantom3.isEnqueued());
        assertFalse(phantom2.isEnqueued());
        assertFalse(phantom4.isEnqueued());

        Reference<? extends Team> reference = null;

        // 중요: 원본 객체가 null인 객체만을 큐에 담아서 따로 후처리가 가능해진다!!
        while ((reference = rq.poll()) != null) {
            System.out.println(reference);
            reference.clear();

            assertTrue(reference == phantom1 || reference == phantom3);
        }
    }
}
