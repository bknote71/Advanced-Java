package ftp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        final FTPServer ftpServer = new FTPServer();
        ftpServer.start();
    }
}
