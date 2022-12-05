package core.socket.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private static final int PORT = 7;

    public static void main(String[] args) {
        ServerSocketChannel serverChannel;
        Selector selector = null;
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket server = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(PORT); // channel을 통한 serverSocket은 binding을 해줘야 한다.
            server.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                // 이벤트 처리
                try {
                    if (key.isAcceptable()) {
                        System.out.println("key is acceptable");
                        final ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        final SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        final SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        clientKey.attach(buffer);
                    }
                    if (key.isReadable()) {
                        System.out.println("key is readable");
                        final SocketChannel client = (SocketChannel) key.channel();
                        final ByteBuffer buffer = (ByteBuffer) key.attachment();
                        client.read(buffer); // client to buffer
                    }
                    if (key.isWritable()) { // write 이벤트는 계속 발생한다.
                        final SocketChannel client = (SocketChannel) key.channel();
                        final ByteBuffer buffer = (ByteBuffer) key.attachment();
                        buffer.flip();
                        client.write(buffer);
                        buffer.compact();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

    }
}
