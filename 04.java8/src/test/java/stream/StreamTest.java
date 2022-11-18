package stream;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Stream: 연속된 데이터를 순차적으로 처리하는 연산의 모음
 * 컬렉션: 데이터 소스
 *
 * 특징:
 * - 원본 데이터 소스의 변화는 없다.
 * - 스트림으로 처리하는 데이터는 오직 한번만 처리
 * = 즉 하나의 operator에서 데이터를 오직 한번만 처리
 * - 데이터가 무제한일 수 있다. (Stream.iterate(seed, Function))
 * - short circuit 메소드를 사용하여 제한할 수 있다. (skip, limit)
 *
 * stream pipeline 정의: 각각의 데이터를 처리하는 연산/작업들을 정의
 * - 중개 operator 및 터미널 operator 로 정의한다.
 *
 * 중개 operator:
 * - Stream을 리턴
 * - Lazy하다.
 * - stream pipeline을 정의하는데, 터미널 operator가 반드시 와야 실행된다.
 * - 중개 operator는 pipeline 정의만. 실행은 반드시 터미널 operator가 있어야 한다.
 * - filter, map, flatmap, .
 *
 * 터미널 operator
 * - Stream을 리턴하지 않는다.
 * - forEach, count, collect
 *
 * [결론]: 중개 및 터미널 operator를 활용하여 스트림 파이프라인을 정의하면 된다.
 * 
 * 손쉽게 병렬 처리
 * - parallelStream()
 * - 내부적 spliterator가 데이터를 쪼개서 병렬적으로 처리
 * - ForkJoinPool (스레드 풀)을 사용하여 병렬적으로 처리
 * - 스레드는 자체적으로 오버헤드가 발생하기 때문에 이 병렬스트림이 오히려 느려질 수도 있다.
 * - 언제 유용? 데이터가 매우 방대할 경우? 직접 성능 측정을 하면서 진행해야 한다.
 *
 * flatMap(Function<?, ? extends Stream<?>>)
 * - 컬렉션인 데이터 원소를 컬렉션 내부에 있는 데이터 원자로 쪼개는 것
 * - 컬렉션인 하나의 데이터 원소를 Stream으로 변경하면 내부적으로 개별적인 원자 데이터로 쪼갠다.
 * - collection -> stream -> <원자 데이터>
 */
public class StreamTest {

    List<OnlineClass> classes = new ArrayList<>();
    List<OnlineClass> classes2 = new ArrayList<>();

    // BeforeEach
    @Before
    public void init() {
        classes.add(new OnlineClass(1, "spring boot", true));
        classes.add(new OnlineClass(2, "spring data jpa", true));
        classes.add(new OnlineClass(3, "spring mvc", false));
        classes.add(new OnlineClass(4, "spring principle", true));
        classes.add(new OnlineClass(5, "kafka", false));

    }

    @Test
    public void CountingStartWithSpring() {
        // "spring"으로 시작하는 수업 개수
        final long springClass = classes.stream()
                .filter(c -> c.getTitle().startsWith("spring"))
                .count();

        assertEquals(4, springClass);

        // 데이터 변화 없음
        System.out.println(classes);
        assertEquals(5, classes.size());

        System.out.println("============next============");

        classes.stream()
                .map(OnlineClass::getTitle) // 임의의 객체의 인스턴스 메서드 대한 참조
                .filter(s -> s.startsWith("spring"))
                .forEach(System.out::println);
    }

    @Test
    public void CountingNonClosed() {
        final long count = classes.stream()
                .filter(c -> !c.isClosed())
                .count();

        assertEquals(2, count);

        // Predicate.not(PredicateA): !PredicateA
        classes.stream()
                // .filter(!OnlineClass::isClosed) // !PredicateA 는 문법적으로 막혀있다.
                .filter(Predicate.not(OnlineClass::isClosed))// 임의의 객체의 인스턴스 메서드 대한 참조
                .forEach(c -> System.out.println(c.getId()));

        assertEquals(5, classes.size());
    }

    @Test
    public void collectClassName() {
        final List<String> collect = classes.stream()
                .map(OnlineClass::getTitle)
                .collect(Collectors.toList());

        assertEquals(5, collect.size());
        System.out.println(collect);
    }

    @Test
    public void flatMap() {
        // flatMap: 하나의 컬렉션 데이터를 원자 데이터로 쪼개는 방법
        // list -> stream으로 변환하는 작업을 해야한다.
        classes2.add(new OnlineClass(6, "async", false));
        classes2.add(new OnlineClass(7, "blockchain", false));
        classes2.add(new OnlineClass(8, "coffee", true));


        List<List<OnlineClass>> myClasses = new ArrayList<>();
        myClasses.add(classes);
        myClasses.add(classes2);

        myClasses.stream()
                .flatMap(Collection::stream) // list -> list.stream()
                .map(OnlineClass::getTitle)
                .forEach(System.out::println);
    }

    @Test
    public void parallelStream() {
        classes.parallelStream()
                .forEach(c -> {
                    System.out.println(c.getTitle() + ": " + Thread.currentThread().getName());
                });
    }

    @Test
    public void iterate() {
        // iterate(T seed, UnaryOperator<? extends T>): 무제한 스트림 (seed 를 가져와서 내부적으로 무언가를 생산하는 생산자 역할=>? extends T)
        Stream.iterate(10, i -> i + 1) // seed 부터 시작, 다음부터 데이터 변환(using UnaryOperator)
                .skip(10)
                .limit(10)
                .forEach(System.out::println);
    }

}