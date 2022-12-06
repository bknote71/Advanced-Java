package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPServer {

    private static final int PORT = 21;

    public void start() {
        // thread pool
        final ExecutorService es = Executors.newFixedThreadPool(5);

        try (final ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                // print
                System.out.println(socket.getInetAddress().getHostAddress());
                System.out.println(socket.getLocalPort());
                es.submit(new ServerPI(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
