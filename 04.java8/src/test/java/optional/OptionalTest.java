package optional;

import org.junit.Test;

import javax.swing.text.html.Option;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;


public class OptionalTest {

    // Optional.of(null) --> NullPointerException 발생 (Objects.requireNonNull(value))
    // Optional.ofNullable()을 사용하는 것이 좋겠다. 
    // null을 넣으면 --> EMPTY 로 변경

    @Test
    public void nullPointByReturnValue() {
        final OnlineBook onlineBook = new OnlineBook("데이터 통신");

        // book store 의
        assertThrows(NullPointerException.class, () -> onlineBook.getBook().getTitle());
    }

    @Test
    public void optionalOfNullBook() {
        final OnlineBook onlineBook = new OnlineBook("데이터 통신");

        // Optional.of(null) --> NullPointerException 발생 (Objects.requireNonNull(value))
        assertThrows(NullPointerException.class, onlineBook::optionalOfBook);
    }

    @Test
    public void optionalOfNullableBook() {
        final OnlineBook onlineBook = new OnlineBook("데이터 통신");

        Optional<Book> optional = onlineBook.optionalOfNullableBook();

        // isPresent == !isEmpty
        assertTrue(!optional.isPresent());
        assertTrue(optional.isEmpty());
    }

    @Test
    public void optionalGet() {
        final OnlineBook onlineBook = new OnlineBook("데이터 통신");

        Optional<Book> optional = onlineBook.optionalOfNullableBook();

        // 비어있는 Optional의 get() 메서드 호출 시 NoSuchElementException 발생
        // 따라서 바로 get()을 통하여 값을 꺼내는 것이 아닌 isPresent, isEmpty, .. 같은 메서드로 값을 체크하고 값을 꺼내야 한다.
        // 아니면 ifPresent 같이 get()을 사용하지 않고 Optional의 기능을 통해 스무스하게 내부 값을 사용할 수도 있다. <-- 권장
        assertThrows(NoSuchElementException.class, optional::get);
    }

    // smooth optional api
    @Test
    public void ifPresent() {
        final OnlineBook onlineBook = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));

        Optional<Book> optional = onlineBook.optionalOfNullableBook();

        // not smooth
        if (optional.isPresent()) {
            final Book book = optional.get();
            System.out.println(book);
        }

        // smooth optional api
        optional.ifPresent(System.out::println);
    }

    @Test
    public void orElse() {
        final OnlineBook onlineBook1 = new OnlineBook("데이터 통신");
        final OnlineBook onlineBook2 = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));

        Optional<Book> optional = onlineBook1.optionalOfNullableBook();

        // orElse(T): 값이 없으면 T 리턴
        final Book book = optional.orElse(new Book("default", "bk", 0));
        System.out.println(book);
    }

    @Test
    public void orElseGet() {
        final OnlineBook onlineBook1 = new OnlineBook("데이터 통신");
        final OnlineBook onlineBook2 = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));

        Optional<Book> optional = onlineBook1.optionalOfNullableBook();

        // orElseGet(Supplier): 값이 없으면 Supplier 결과값 리턴
        final Book book = optional.orElseGet(Book::new);
        System.out.println(book);
    }

    @Test
    public void orElseThrow() {
        final OnlineBook onlineBook1 = new OnlineBook("데이터 통신");
        final OnlineBook onlineBook2 = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));

        Optional<Book> optional = onlineBook1.optionalOfNullableBook();

        // orElseThrow(Supplier ex): 값이 없으면 Supplier 결과값(ex) 리턴 = 예외 리턴
        assertThrows(NoSuchElementException.class, () -> optional.orElseThrow(NoSuchElementException::new));
    }

    @Test
    public void filter() {
        final OnlineBook onlineBook1 = new OnlineBook("데이터 통신");
        final OnlineBook onlineBook2 = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));
        final OnlineBook onlineBook3 = new OnlineBook("데이터 통신", new Book("data", "bk", 10));
        final OnlineBook onlineBook4 = new OnlineBook("데이터 통신", new Book("data", "bk", 100000));

        final Optional<Book> optional1 = onlineBook1.optionalOfNullableBook();
        final Optional<Book> optional2 = onlineBook2.optionalOfNullableBook();
        final Optional<Book> optional3 = onlineBook3.optionalOfNullableBook();
        final Optional<Book> optional4 = onlineBook4.optionalOfNullableBook();

        // filter(predicate): Optional 반환 (predicate 조건에 통과하지 못하면 내부 값을 EMPTY 로 변경, 통과하면 그대로)
        // 애초부터 값이 EMPTY 이면 predicate 조건절을 통과시키지 않는다. (즉 EMPTY 그대로^^)

        Predicate<Book> predicate = b -> {
            System.out.println("predicate");
            return b.getPrice() > 500;
        };

        final Optional<Book> optionall1 = optional1.filter(predicate);
        final Optional<Book> optionall2 = optional2.filter(predicate);
        final Optional<Book> optionall3 = optional3.filter(predicate);
        final Optional<Book> optionall4 = optional4.filter(predicate);

        // 1, 3은 통과 못함, 2, 4는 통과
        assertTrue(optionall1.isEmpty());
        assertTrue(optionall3.isEmpty());
        assertFalse(optionall2.isEmpty());
        assertFalse(optionall4.isEmpty());
    }

    @Test
    public void mapAndFlatMap() {
        final OnlineBook onlineBook1 = new OnlineBook("데이터 통신");
        final OnlineBook onlineBook2 = new OnlineBook("데이터 통신", new Book("data", "bk", 1000));

        final Optional<Book> optional1 = onlineBook1.optionalOfNullableBook();
        final Optional<Book> optional2 = onlineBook2.optionalOfNullableBook();

        // 환율때문에 돈 1000원 추가
        Function<Book, Book> function = b -> {
            System.out.println("increment price");
            // primitive wrapper type is immutable
            b.incrementPrice(1000);
            return b;
        };

        // EMPTY 이면 진행하지 않는다.
        optional1.map(function);
        optional2.map(function);

        // flatMap: 평탄화, map의 리턴값이 Optional일 때 Optional을 벗겨준다.
        final Optional<Optional<Book>> book = optional2.map(b -> Optional.ofNullable(b));
        final Optional<Book> optional = optional2.flatMap(b -> Optional.ofNullable(b));
    }

}