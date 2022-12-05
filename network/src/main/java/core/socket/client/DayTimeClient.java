package core.socket.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DayTimeClient {
    public static void main(String[] args) throws ParseException {
        try (Socket socket = new Socket("time.nist.gov", 13)) {
            socket.setSoTimeout(15000);
            // reader 도 close 해야하지 않나?
            try (final InputStreamReader r = new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)) {
                final StringBuilder time = new StringBuilder();
                for (int c = r.read(); c != -1; c = r.read()) {
                    time.append((char) c);
                }
                final Date date = parseDate(time.toString());
                System.out.println(date);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Date parseDate(String s) throws ParseException {
        final String[] pieces = s.split(" ");
        final String dateTime = pieces[1] + " " + pieces[2] + " UTC";
        final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss z");
        return format.parse(dateTime);
    }
}
