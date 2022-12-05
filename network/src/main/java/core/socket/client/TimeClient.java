package core.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class TimeClient {
    private static final String HOSTNAME = "time.nist.gov";

    public static void main(String[] args) {
        final Date date = TimeClient.getDateFromNetwork();
        System.out.println(date);
    }

    public static Date getDateFromNetwork() {
        // 타임 프로토콜은 1900년을 기준으로 하지만, 자바의 Date는 1970년대를 기준으로 한다.
        // 아래 숫자는 시간을 변환하는 데 사용된다.
        long differenceBetweenEpochs = 2208988800L;
        // 32비트 부호없는 빅엔디안 이진 숫자를 보낸다. = 4 byte를 준다.
        try (final Socket socket = new Socket(HOSTNAME, 37)){
            socket.setSoTimeout(15000);
            try (final InputStream in = socket.getInputStream()) {
                long secondsSince1900 = 0;
                for (int i = 0; i < 4; ++i) {
                    secondsSince1900 = (secondsSince1900 << 8) | in.read();
                }
                long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
                long msSince1970 = secondsSince1970 * 1000;
                final Date date = new Date(msSince1970);// ms(밀리세컨드) 단위
                return date;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
