package ftp.handler;

import ftp.ServerPI;
import ftp.cmd.NoArgsCommand;

import java.io.IOException;
import java.net.ServerSocket;

public class TransferParameterHandler {

    private ServerPI pi;

    private ServerSocket server; // pasv 에서 생성

    // transfer parameter: type, mode, stru, form
    String type; // A(ascii or eb..), I(binary), L(logical byte?)
    String mode; // stream, block, compressed
    String stru; // file, record, page
    String form; // type A: vertical format --> non-print, telnet control,

    public boolean isAscii() {
        return type.equals("A");
    }

    public boolean isBinary() {
        return type.equals("I");
    }

    public void register() {
        pi.registerCommand("PASV", (NoArgsCommand) this::pasv); // pasv는 this의 내부 상태에 따라 동작이 달라진다.
    }

    private void pasv() {
        try {
            server = new ServerSocket(0);

            final String host = server.getInetAddress().getHostAddress();
            final int port = server.getLocalPort();

            final String address = host.replaceAll(".", ",");
            final String adPort = port / 256 + "," + port % 256;

            pi.sendResponse(227, "(" + address + "," + adPort + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
