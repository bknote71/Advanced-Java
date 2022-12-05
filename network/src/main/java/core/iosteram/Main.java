package core.iosteram;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // BufferedOutputStream
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("test.txt")))) {
            dos.write(10);
            dos.write(-129);
            dos.writeInt(-3);
            dos.writeFloat((float) 7.9);
            dos.writeBoolean(true);
            dos.writeByte(127);
            dos.writeUTF("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (final DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("test.txt")))) {
            byte[] bytes = new byte[2];
            dis.read(bytes);

            System.out.println(Arrays.toString(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
