package ftp.handler;

import ftp.ServerPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileServiceHandler {

    private static final String DEFAULT_PATH = "/";

    private ServerPI pi;
    private String path = DEFAULT_PATH;

    public FileServiceHandler(ServerPI serverPI) {
        this.pi = serverPI;
    }

    public void register() {
        pi.registerCommand("RETR", this::retr, true);
    }

    public void retr(String path) {
        final File file = new File(path);
        try (final FileInputStream in = new FileInputStream(file)) {
            pi.sendResponse(150, "send");
            pi.sendData(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
