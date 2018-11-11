package space.pankui.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author pankui
 * @date 2018/11/11
 * <pre>
 *
 * </pre>
 */
public class SelectorDemo {

    public static void main(String[] args) throws IOException {


        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(8080));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int ready = selector.select();
            if (ready == 0) {
                continue;
            } else if (ready < 0) {
                break;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {

                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    if (accept == null) {
                        continue;
                    }
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // 读事件
                    deal((SocketChannel) key.channel(), key);
                } else if (key.isWritable()) {
                    // 写事件
                    resp((SocketChannel) key.channel(), key);
                }
                // 注：处理完成后要从中移除掉
                iterator.remove();
            }
        }
        selector.close();
        socketChannel.close();
    }

    private static void deal(SocketChannel channel, SelectionKey key) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);

        int read = channel.read(buffer);

        if (read > 0) {
            buffer.flip();
            responseBuffer.put(buffer);
        } else if (read == -1) {
            System.out.println("socket close");
            channel.close();
            return;
        }

        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        key.attach(responseBuffer);
    }

    private static void resp(SocketChannel channel, SelectionKey key) throws IOException {

        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();

        channel.write(buffer);
        if (!buffer.hasRemaining()) {
            key.attach(null);
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
