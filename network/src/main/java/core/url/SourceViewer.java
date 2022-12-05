package core.url;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class SourceViewer {
    public static void main(String[] args) {
        try {
            // protocol이 틀리면 MalformedURLException
            // 중요: 나머지 형식이 틀리면 스트림을 열 때 (openConnection, socket 연결 시) UnknownHostException이 발생한다.
            final URL url = new URL("http://www.cafeaulait.org/books/jnp4/examples/index.html");
            try (Reader r = new InputStreamReader(new BufferedInputStream(url.openStream()))) {
                int c;

                System.out.println(url.getProtocol());
                System.out.println(url.getHost());
                System.out.println(url.getPort());
                System.out.println(url.getDefaultPort());
                System.out.println(url.getPath());
                System.out.println(url.getFile());
                System.out.println(url.getQuery());
                System.out.println(url.getAuthority());
                System.out.println(url.toExternalForm());

                while ((c = r.read()) != -1) {
                    System.out.print((char) c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
