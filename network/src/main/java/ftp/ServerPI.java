package ftp;

import ftp.cmd.Command;
import ftp.handler.TransferParameterHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerPI implements Runnable, AutoCloseable {

    private final Map<String, Command> commandRepo = new HashMap<>();

    private final Socket socket;
    private final BufferedReader r;
    private final BufferedWriter w;

    private final TransferParameterHandler transferParameterHandler;

    public ServerPI(Socket socket) throws IOException {
        this.socket = socket;
        r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        transferParameterHandler = new TransferParameterHandler(this);

        transferParameterHandler.register();
    }

    public void sendResponse(int code, String message) {
        // response check
        try {
            w.write(code + " " + message + "\r\n");
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("갑자기 연결이 끊김2?");
        }
    }

    @Override
    public void run() {
        // request 읽는다.
        while (!socket.isClosed()) {
            acceptRequest();
        }

        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptRequest() {
        try {
            final String line = r.readLine().toUpperCase();
            if (line.equals(" ")) // just enter \r\n
                return;

            final int sp = line.indexOf(" ");
            final String label = line.substring(0, sp);
            Command command = commandRepo.get(label);
            if (command == null) {
                sendResponse(520, "Unknown command");
                return;
            }
            processRequest(command, sp != -1 || sp == line.length() ? line.substring(sp + 1) : null);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("갑자기 연결이 끊김1?");
        }
    }


    private void processRequest(Command cmd, String args) {
        cmd.execute(args);
    }

    public void registerCommand(String label, Command cmd) {
        this.commandRepo.put(label, cmd);
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
