package core.share.escaped;

import java.util.EventListener;

public class ThisEscape {

    private EscapedReference escapedReference = new EscapedReference(11111);

    // 생성자뿐만 아니라 여러 방법으로 inner 객체를 외부로 유출할 수 있다.
    // ex) 아래처럼 외부 매개변수, 반환값
    public ThisEscape(Client client) {

        // 내부 클래스의 인스턴스를 외부에 공개하는 경우 부모 객체의 상태가 외부로 유출될 수 있다.
        client.addEventListener(
                // local inner class
                new MyEventListener() {
                    @Override
                    public EscapedReference getEscapedReference() {
                        return escapedReference;
                    }
                });

    }

    // 비교용 getter
    public EscapedReference getEscapedReference() {
        return escapedReference;
    }


}
