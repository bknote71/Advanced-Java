package core.share.escaped;

import java.util.EventListener;

public class Client {

    private MyEventListener eventListener;

    public void addEventListener(MyEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public MyEventListener getEventListener() {
        return eventListener;
    }

    public void add10000() {
        for (int i = 0; i < 10000; ++i) {
            eventListener.getEscapedReference().increment();
        }
    }
}
