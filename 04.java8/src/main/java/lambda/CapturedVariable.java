package lambda;

import java.util.function.UnaryOperator;

public class CapturedVariable {
    private int fieldNumber = 10;

    /**
     * 변수 캡처:
     * - 람다 내부에서 람다를 감싸는 블록(외부)의 "로컬 변수"를 참조, 사용할 때 캡처된다.
     * - 익명/로컬 내부 클래스에서도 쓰이는 기능이다. (로컬 변수 참조)
     * - 로컬 변수를 사용하기 위해서는 자바8 이전에는 항상 final 키워드가 있어야 했다.
     *
     * "자바8"부터는 final을 생략할 수 있다.
     * - 생략 가능 경우: 사실상 final인 경우 = 해당 로컬 변수를 변경하는 코드가 없는 경우
     * - effective final
     */

    public void plusBaseNumberByLambda(Integer integer) {
        // 람다에서 외부 로컬 변수를 사용할 경우 final 이라고 가정해서 사용한다.
        // 따라서 변경할 수 없게 한다.
        int baseNumber = 10;
        UnaryOperator<Integer> op = i -> i + baseNumber;

        // baseNumber는 람다 내부에서 사용되었기 때문에 final이다.
        // 따라서 변경하는 코드가 없어야 하는데 있으면 컴파일 에러가 발생한다.
        // baseNumber++;

        System.out.println("lambda: " + op.apply(integer));
    }

    public void plusBaseNumberByLocal(Integer integer) {
        // 참고: 익명/로컬 클래스는 내부 클래스이다.
        int baseNumber = 10;
        UnaryOperator<Integer> op = i -> i + baseNumber;

        class LocalClass {
            // "integer"는 쉐도잉이 적용된다.
            int plusBaseNumber(Integer integer) {
                return integer + baseNumber;
            }
        }

        // baseNumber++;
        System.out.println("anonymous inner: " + op.apply(integer));
        System.out.println("local inner: " + new LocalClass().plusBaseNumber(integer));
    }

    /**
     * 람다는 익명 클래스를 줄인 표현식이다.
     *
     * 람다는 로컬변수를 캡처한다.
     * - 변경할 수 없다.
     * - 멱등성을 보장한다.
     *
     * 멤버 변수는? 람다 내부에서 변경이 가능하다.
     *
     * 멤버 변수를 참조? 순수하지 못한 함수 (멱등성을 보장하지 못한다.)
     * 순수하지 못한 함수:
     * - 어떤 상태값에 의존할 때
     * - 외부의 값을 변경하려할 때
     */
    public void plusFieldNumberByLambda(Integer integer) {

        UnaryOperator<Integer> op = i -> {
            return i + ++fieldNumber;
        };

        System.out.println("field by Lambda: " + op.apply(integer));
    }

    public void plusFieldNumberByLocal(Integer integer) {

        class LocalClass {
            // "integer"는 쉐도잉이 적용된다.
            int plusBaseNumber(Integer integer) {
                return integer + (++fieldNumber);
            }
        }

        System.out.println("field by Local: " + new LocalClass().plusBaseNumber(integer));
    }

    public int getFieldNumber() {
        return fieldNumber;
    }


    public static void main(String[] args) {
        final CapturedVariable capturedVariable = new CapturedVariable();

        // 멱등성을 보장한다: 순수한 함수
        // 멱등성? 입력 같을 때 항상 같은 결과값 리턴
        capturedVariable.plusBaseNumberByLambda(10);
        capturedVariable.plusBaseNumberByLambda(10);
        capturedVariable.plusBaseNumberByLocal(10);
        capturedVariable.plusBaseNumberByLocal(10);

        System.out.println("======================================================================");

        // 멱등성을 보장하지 못한다: 순수하지 못한 함수
        capturedVariable.plusFieldNumberByLambda(10);
        capturedVariable.plusFieldNumberByLambda(10);
        capturedVariable.plusFieldNumberByLocal(10);
        capturedVariable.plusFieldNumberByLocal(10);

        System.out.println("get filed number: " + capturedVariable.getFieldNumber());

    }
}
