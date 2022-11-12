package core.share.escaped;

import static org.junit.Assert.*;
import org.junit.Test;

// UnsafeEscapedStatus 객체를 공개:
// 객체 내부의 private이 아닌 변수나 메서드를 통해 불러올 수 있는 모든 객체(상태)는 함께 공개/유출된다.
public class UnsafeEscapedStatusTest {

    // 1. public 변수를 통해 객체/상태 공개하기
    @Test
    public void publicVariable() throws InterruptedException {
        final UnsafeEscapedStatus.UnsafeInstance status = UnsafeEscapedStatus.status;

        int numOfThread = 10;
        for (int i = 0; i < 10; ++i) {
            new Thread(status::add10000).start();
        }

        Thread.sleep(100);
        assertNotEquals(numOfThread * status.getIterCount(), status.getValue());
        System.out.println(status.getValue());
    }

    // 2. pubic method를 통해 객체/상태 공개하기
    @Test
    public void publicMethod() throws InterruptedException {
        final UnsafeEscapedStatus unsafeEscapedStatus = new UnsafeEscapedStatus();
        final UnsafeEscapedStatus.UnsafeInstance status = unsafeEscapedStatus.getStatus();

        int numOfThread = 10;
        for (int i = 0; i < 10; ++i) {
            new Thread(status::add10000).start();
        }

        Thread.sleep(100);
        assertNotEquals(numOfThread * status.getIterCount(), status.getValue());
        System.out.println(status.getValue());
    }
}