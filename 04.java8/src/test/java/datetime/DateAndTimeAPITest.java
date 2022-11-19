package datetime;

import org.junit.Test;

import java.sql.Time;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DateAndTimeAPITest {

    @Test
    public void DateTimeApiBeforeJava8() {
        // Date 특징
        // 1. 명확하지 않다.
        // Date api는 영어로 날짜인데 실제로는 시간까지 다루고 기계가 이해하는 시간이다.
        final Date date = new Date();
        final long time = date.getTime();

        System.out.println(time);

        // 2. mutable 하다.
        // - thread-safe하지 않다.
        Date myDate = new Date();
        Date newDate = new Date();

        myDate = newDate;

        // 3. 버그가 발생할 여지
        // 타입 안정성이 없다. 월이 0부터 시작, 제한이 있는 날짜를 int로 받는다던가 ..
        final Date date1 = new Date(2022, 10, 10);
        System.out.println(date1);

        // month: 12이면 실제로는 13월이다.
        // --> 12 % 12  + 1 => 1월로 취급
        final Date date2 = new Date(2022, 12, 10);
        System.out.println(date2);

        Calendar calendar = new GregorianCalendar();
        final Date time1 = calendar.getTime();
        System.out.println(time1);

        // 위와같은 불편함 때문에 java8 이전에는 joda-time을 사용했다고 한다.
    }

    /**
     * java8 이후에는 joda-time이 자바 표준(jsr-310) 스팩에 들어갔다고 한다.
     * java8 Date-
     * Time API: jsr-310 스팩의 구현체 제공
     *
     * 디자인 철학:
     * clear: 날짜면 날짜, 시간이면 시간, API이름에 맞게 명확하게 동작한다.
     * fluent: 유연한 API 지원 (e.g. 기계용 시간, 사람용 시간, ..)
     * immutable: 불변 = 스레드 안정
     * extensible: 여러 존(지역) 지원 + Calendar: 음력, 양력, 불교달력, .. 여러 상황을 지원한다.
     */

    @Test
    public void DateTimeApiAfterJava8() throws InterruptedException {
        // 사람용 시간: 연, 월, 일, 시, 분, 초
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);

        LocalTime localTime = LocalTime.now();
        System.out.println(localTime);

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        // immutable: 값 변경 시 새로운 참조를 리턴
        Thread.sleep(1000);
        localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        // immutable: 상태 변경 --> 새로운 참조
        // 날짜 변경: 반드시 새로운 변수로 참조해야됨. plus 한다고 더해지는 것이 아님
        LocalDateTime plus = localDateTime.plus(10, ChronoUnit.DAYS);
        System.out.println(plus);
        System.out.println(localDateTime);
    }

    @Test
    public void zoneAndInstant() {
        // 기계용 시간: Instant
        final Instant instant = Instant.now();
        System.out.println(instant);

        // 특정 존(지역) 기존으로 시간을 변경할 수 있다.
        instant.atZone(ZoneId.of("Asia/Seoul"));
        System.out.println(instant);

        instant.atZone(ZoneId.systemDefault());
        System.out.println(instant);

        // ZonedDateTime <-> Instant
        
        // 레거시 코드와 호환
        // Date <-> Instant <-> ZonedDateTime -> Local(date,time,datetime)

        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final Instant instant1 = zonedDateTime.toInstant();
        final ZonedDateTime zonedDateTime1 = instant1.atZone(ZoneId.systemDefault());

        final LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        final ZonedDateTime zonedDateTime2 = localDateTime.atZone(ZoneId.systemDefault());

        // Calendar <-> instant <-> zoneDateTime <-> LocalDateTime
        Calendar calendar = new GregorianCalendar();
        final LocalDateTime localDateTime1 = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println(localDateTime1);
    }
}