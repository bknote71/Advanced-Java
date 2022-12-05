package core.inet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WebLogger {
    /**
     * InetAddress.getByName(호스트네임):
     * - 호스트네임에 해당하는 IP 주소가 없으면 UnknownHostException이 발생한다. (런타임 예외)
     * getByName(ip주소):
     * - ip에 대응하는 실제 호스트가 존재하는지 보장하지 않는다. 신경쓰지 않는다는 소리, 단 잘못된 크기인 경우에만 UnknownHostException
     * getByAddress(byte[] or (hostname, byte[])):
     * - 이것도 마찬가지로 실제 호스트가 존재하는지 또는 호스트 네임과 IP주소가 올바른 연결이 맞는지 보장하지 않는다.
     * - 주소 인자로 전달된 바이트 배열이 잘못된 크기인 경우에만 UnknownHostException
     * <p>
     * ip 주소로 address를 얻었을 때
     * getHostName:
     * - ip 주소에 해당하는 호스트 네임을 포함한 String 을 반환한다.
     * - 만약 dns 서버 장비가 호스트 네임을 가지고 있지 않거나 보안 관리자가 이름 검색을 막을 경우 마침표로 구분된 네 자리 IP 주소가 반환된다.
     * - 즉 검색에 사용한 주소를 다시 반환한다.
     */
    public static void main(String[] args) {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            for (int i = 0; i < 5; ++i) {
                final String line = br.readLine();
                try {
                    final InetAddress byName = InetAddress.getByName(line);
                    System.out.println(byName.getHostAddress());
                    System.out.println(byName.getHostName());
                } catch (UnknownHostException ex) {
                    System.err.println(ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
