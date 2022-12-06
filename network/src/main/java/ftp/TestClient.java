package ftp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String[] args) {
        try (final Socket socket = new Socket("localhost", 21)){
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                final String line = br.readLine();
                w.write(line + " \r\n");
                w.flush();

                final String s = r.readLine();
                System.out.println(s);
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
