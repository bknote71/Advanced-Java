package core.inet;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SpamCheck {
    private static final String BLACKHOLE = "www.naver.com";
    public static void main(String[] args) {
        for (String arg : args) {
            if (isSpammer(arg))
                System.out.println(arg + " is a know spammer.");
             else
                System.out.println(arg + " is not a spammer");
        }
    }

    private static boolean isSpammer(String arg) {
        try {
            final InetAddress address = InetAddress.getByName(arg);
            final byte[] bytes = address.getAddress();
            String query = BLACKHOLE;
            for (byte b : bytes) {
                query = b + "." + query;
            }
            InetAddress.getByName(query);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
