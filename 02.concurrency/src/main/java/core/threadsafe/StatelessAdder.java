package core.threadsafe;

public class StatelessAdder {
    // 변경 가능한 공유 변수가 없다.
    // 공유하는 상태가 없는 객체는 항상 스레드 안전하다,

    // 여러 스레드가 상태를 공유하지 않기 때문에 사실상 서로 다른 인스턴스에 접근하는 것과 같다.
    public int add1000(int value) {
        for (int i = 0; i < 1000; ++i) {
            value += 1;
        }
        return value;
    }

    // 여러 스레드가 Integer 라는 상태(변수)를 공유하지만 Integer 연산(+,-,..)은 다른 Integer 객체를 산출하기 때문에
    // 결과적으로 상태를 공유하지 않은 것과 같게 된다.
    public int add10000toInteger(Integer value) {
        for (int i = 0; i < 10000; ++i) {
            value += 1;
        }
        return value;
    }

    // 필드로 공유하는 상태를 가지지 않는다 해도, 매개변수로 상태를 공유할 수 있다.
    // --> 스레드 안전하지 않다.
    // 즉 공유하는 상태가 없는 객체라는 의미는 매개변수도 포함한다.
    // 물론 매개변수로 공유하는 상태가 불변 객체이면 스레드 안전할 수 있다.
    public int add10000toValueHolder(ValueHolder valueHolder) {
        for (int i = 0; i < 10000; ++i) {
            valueHolder.value += 1;
        }
        return valueHolder.value;
    }

    public static class ValueHolder {
        int value;

        public ValueHolder(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
