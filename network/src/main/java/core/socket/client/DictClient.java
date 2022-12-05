package core.socket.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class DictClient {
    private static final String HOSTNAME = "dict.org";
    private static final int PORT = 2628;
    private static final int TIMEOUT = 15000;

    public static void main(String[] args) {
        // socket.close(): 입력과 출력을 모두 닫는다.
        // 즉 original inputStream, outputStream을 닫는다.
        // 원래 original stream + filter stream + filter stream + .. + 이렇게 겹쳐진 형태이면
        // 가장 바깥족 stream을 닫으면 결국에는 original stream 이 닫히는 구조이다.
        try (final Socket socket = new Socket(HOSTNAME, PORT)) {
//            socket.setSoTimeout(TIMEOUT);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            try (final BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {


                while (true) {
                    final String in = input.readLine();
                    writer.write(in + "\r\n");
                    writer.flush();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (!define(line)) break;
                    }
                    System.out.println("end");
                    if (in.equals("quit")) // 서버가 종료되었음: 커넥션을 끈는다. (당연히 즉시 끊어지는 것이 아니고 CLOSE 단계를 거치고 끊어짐.)
                        break;
                    System.out.println(socket.isInputShutdown());
                    System.out.println(socket.isOutputShutdown());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean define(String line) {
        if (line.startsWith("250 ")) return false;
        else if (line.startsWith("552 ")) {
            System.out.println("No definition found");
            return false;
        } else if (line.matches("\\d\\d\\d .*") || line.trim().equals(".")) return true;
        else System.out.println(line);
        return true;
    }


}
