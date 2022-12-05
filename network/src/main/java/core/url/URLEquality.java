package core.url;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

public class URLEquality {
    public static void main(String[] args) throws MalformedURLException {
        // URL 비교는 DNS 쿼리로 비교한다
        // - 블로킹 I/O
        // - 근데? 식별된 리소스는 비교하지 않는다.
        // 경로가 다르면 다르다.
        // 포트가 다르면 다르다.
        final URL u1 = new URL("http://www.ibiblio.org/");
        final URL u2 = new URL("http://ibiblio.org/");

        System.out.println(u1.equals(u2));

        final URL u3 = new URL("http://www.oreilly.com/");
        final URL u4 = new URL("http://www.oreilly.com/index.html");

        System.out.println(u3.equals(u4));
    }
}
