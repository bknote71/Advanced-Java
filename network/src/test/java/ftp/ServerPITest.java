package ftp;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ServerPITest {

    @Test
    public void stringtrim() {
        String s = "retr C:\\Users\\HP NOTE\\Desktop\\adv-java\\network\\test.txt";
        final int i = s.indexOf(" ");
        System.out.println(i);
    }

    @Test
    public void fileRead() throws IOException {
        String s = "C:\\Users\\HP NOTE\\Desktop\\adv-java\\network\\test.txt";
        final File file = new File(s);
        final FileInputStream in = new FileInputStream(file);
        final BufferedOutputStream out = new BufferedOutputStream(System.out);
        int c;
        byte[] buf = new byte[100];
        while ((c = in.read(buf)) != -1) {
            System.out.println(c);
            System.out.println(Arrays.toString(buf));
            System.out.println("--");
//            write(out, buf, c);
            out.write(buf, 0, c);
            out.flush();
        }


    }

    private void write(OutputStream out, byte[] buf, int c) {
        for (int i = 0; i < c; ++c) {

        }
    }

}