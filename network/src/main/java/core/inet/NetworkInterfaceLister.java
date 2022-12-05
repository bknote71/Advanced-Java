package core.inet;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkInterfaceLister {
    public static void main(String[] args) {
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface ni = interfaces.nextElement();
                System.out.println(ni.getIndex() + ": " + ni);
                final Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    System.out.println(inetAddresses.nextElement());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
