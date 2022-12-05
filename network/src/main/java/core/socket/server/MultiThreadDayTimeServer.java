package core.socket.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class MultiThreadDayTimeServer {
    private static final int PORT = 13;

    public static void main(String[] args) {
        try (final ServerSocket server = new ServerSocket(PORT)){
            while (true) {
                Socket socket = server.accept();
                Runnable task = new DayTimeThread(socket);
                new Thread(task).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class DayTimeThread implements Runnable {

        Socket socket;

        public DayTimeThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Writer writer = new OutputStreamWriter(socket.getOutputStream())) {
                final Date now = new Date();
                writer.write(now.toString() + "\r\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
