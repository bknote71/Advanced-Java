package ftp.handler;

import ftp.ServerPI;
import ftp.auth.AuthenticationManager;
import ftp.auth.BasicAuthenticationManager;
import ftp.cmd.NoArgsCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TransferParameterHandler {

    private ServerPI pi;

    private ServerSocket server; // pasv 에서 생성

    // auth: access control
    private String username;
    private String password;
    private boolean loggedOn = false;
    private AuthenticationManager authenticationManager;

    // transfer parameter: type, mode, stru, form
    String type; // A(ascii or eb..), I(binary), L(logical byte?)
    String mode; // stream, block, compressed
    String stru; // file, record, page
    String form; // type A: vertical format --> non-print, telnet control,

    public TransferParameterHandler(ServerPI pi) {
        this.pi = pi;
        this.authenticationManager = new BasicAuthenticationManager();
    }

    public Socket socket() throws IOException {
        if (server == null)
            throw new IllegalStateException("아직 데이터 서버 소켓이 연결되지 않았습니다.");
        return server.accept();
    }

    public boolean isLoggedOn() {
        return loggedOn;
    }

    public boolean isAscii() {
        return type.equals("A");
    }

    public boolean isBinary() {
        return type.equals("I");
    }

    public void register() {
        pi.registerCommand("USER", this::user, false);
        pi.registerCommand("PASS", this::pass, false);
        pi.registerCommand("PASV", (NoArgsCommand) this::pasv, true); // pasv는 this의 내부 상태에 따라 동작이 달라진다.
    }

    private void user(String username) {
        if (loggedOn) {
            pi.sendResponse(530, "Can't change from guest user.");
            return;
        }
        this.username = username.toUpperCase();
        // basic auth: password 요구
        pi.sendResponse(331, "Please specify the password.");
    }

    private void pass(String password) {
        if (loggedOn) {
            pi.sendResponse(230, "Already logged in.");
            return;
        }
        if (username == null) {
            pi.sendResponse(503, "Login with USER first.");
            return;
        }
        boolean success = authenticationManager.authenticate(username, password.toUpperCase());
        if (success) {
            pi.sendResponse(230, "Login successful.");
            loggedOn = true;
        }
        else
            pi.sendResponse(530, "Login incorrect.");
    }

    private void pasv() {
        try {
            server = new ServerSocket(11111); // 0: 랜덤 포트, 11111: 일단 테스트하기 쉽게 정해놓음 ㅇㅇ

            final String host = server.getInetAddress().getHostAddress();
            final int port = server.getLocalPort();

            final String address = host.replaceAll("[.]", ",");
            final String adPort = port / 256 + "," + port % 256;

            pi.sendResponse(227, "(" + address + "," + adPort + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
