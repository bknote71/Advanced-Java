package core.share.visibility;

/**
 * 가시성 해결
 * 1. synchronized
 * - synchronized 블럭 진입 시: 블럭 전까지의 모든 쓰기 연산이 업데이트 되고, 메인 메모리로부터 데이터를 가져올 수 있다.
 * - synchronized 블럭 벗어날 시: 메인 메모리로 데이터를 flush 한다.
 * --> 최신의 데이터를 가져오려면 getter에 synchronized를 설정해야 한다.
 * 
 * setter에만 synchronized 설정 시:
 * - synchronized 블럭을 벗어날 때 메인 메모리로 데이터를 flush 한다.
 * - 하지만 getter에는 synchronized가 없기 때문에 메인 메모리로부터 데이터를 가져올 수 없다.
 * --> 이렇게 예측했었는데 틀렸다.
 * --> while문에 내용이 없으면 메인 메모리로부터 데이터를 가져오지 않는데, 내용이 있으면 메인 메모리로부터 데이터를 가져온다.
 *
 * getter에만 synchronized 설정 시:
 * - synchronized 블럭을 진입할 때 블럭 전까지의 모든 쓰기 연산이 업데이트 되고 메인 메모리로부터 데이터를 가져오도록 한다
 * - getter 전에 호출되었던 setter라는 쓰기 연산이 업데이트 된다, 따라서 최신 데이터를 가져올 수 있다.
 * 
 */
public class Visibility {
    private int status = 0;
    private volatile int volatileStatus = 0;
    private int halfStatus = 0;

    public synchronized int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVolatileStatus() {
        return volatileStatus;
    }

    public void setVolatileStatus(int volatileStatus) {
        this.volatileStatus = volatileStatus;
    }

    public int getHalfStatus() {
        return halfStatus;
    }

    public synchronized void setHalfStatus(int halfStatus) {
        this.halfStatus = halfStatus;
    }
}
