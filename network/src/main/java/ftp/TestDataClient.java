package ftp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestDataClient {
    public static void main(String[] args) {
        try (final Socket socket = new Socket("localhost", 11111)){
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String s;
                while ((s = r.readLine()) != null) {
                    System.out.println(s);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
