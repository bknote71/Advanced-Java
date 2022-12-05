package core.socket.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class TimeServer {
    private static final long diff = 2208988800L;
    public static void main(String[] args) throws InterruptedException {
        try (final ServerSocket server = new ServerSocket(11111)) {
            while (true) {
                try (final Socket socket = server.accept()) {
                    final OutputStream out = socket.getOutputStream();
                    final Date date = new Date();
                    long msSince1970 = date.getTime();
                    long secondsSince1970 = msSince1970 / 1000;
                    long secondsSince1900 = secondsSince1970 + diff;
                    byte[] time = new byte[4];
                    time[0] = (byte) ((secondsSince1900 & 0x00000000FF000000L) >> 24);
                    time[1] = (byte) ((secondsSince1900 & 0x0000000000FF0000L) >> 16);
                    time[2] = (byte) ((secondsSince1900 & 0x000000000000FF00L) >> 8);
                    time[3] = (byte) ((secondsSince1900 & 0x00000000000000FFL));
                    System.out.println(Arrays.toString(time));
                    out.write(time);
                    out.flush();
                } catch (IOException e) { }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
