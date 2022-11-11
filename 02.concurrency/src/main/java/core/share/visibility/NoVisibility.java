package core.share.visibility;

public class NoVisibility {
    // 여러 스레드에서 해당 status에 접근할 때, 다른 스레드가 작성한 값을 가져갈 수 있다는 보장이 없다.
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
