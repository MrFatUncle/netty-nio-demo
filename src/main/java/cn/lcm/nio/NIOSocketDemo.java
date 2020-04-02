/**
 * FileName: NIOSocketDemo
 * Author:   liaocm
 * Date:     2020/4/1 18:47
 * Description: NIO通信的demo
 * History:
 */
package cn.lcm.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Iterator;
import java.util.Set;

public class NIOSocketDemo {
    /**
     * NIO服务端
     */
    private static class NIOServer {

        public static void main(String[] args) {
            Selector serverSelector = null;
            Selector clientSelector = null;
            ServerSocketChannel channel = null;
            try {
                //开启Selector
                serverSelector = Selector.open();
                clientSelector = Selector.open();
                //开启ServerSocketChannel（并绑定端口）、设置非阻塞、将channel注册到selector上
                channel = ServerSocketChannel.open();
                channel.socket().bind(new InetSocketAddress(8080));
                channel.configureBlocking(false);
                channel.register(serverSelector, SelectionKey.OP_ACCEPT);

                //Selector判断有无连接
                while (true) {
                    if (serverSelector.select(500) < 1) {
                        continue;
                    }

                    //有连接，则看selector有多少个连接，遍历连接，并看连接对应的Accessable、Readable、Writable等是否有效
                    Set<SelectionKey> selectionKeys = serverSelector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if(key.isAcceptable()) {
                            //将SocketChannel注册到Selector上去
                            accept(key);
                        }
                        if(key.isReadable()) {
                            //读数据
                            read(key);
                        }
                        if(key.isValid() && key.isWritable()) {
                            //写数据
                        }
                        iterator.remove();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (serverSelector != null) {
                        serverSelector.close();
                    }
                    if (clientSelector != null) {
                        clientSelector.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        private static void read(SelectionKey key) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketChannel channel = (SocketChannel) key.channel();
            while(channel.read(buffer) > 0) {
                buffer.flip();
                while(buffer.hasRemaining()) {
                    byte[] b = new byte[buffer.limit()];
                    buffer.get(b);
                    System.out.println(new String(b));
                }
                buffer.compact();
            }
            channel.close();
        }

        private static void accept(SelectionKey key) throws IOException {
            ServerSocketChannel channel = (ServerSocketChannel)key.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }

    }

}

class IOSocketDemo {
    /**
     * IO服务端
     * 服务端使用ServerSocket
     */
    private static class IOServer {

        public static void main(String[] args) {
            ServerSocket serverSocket = null;
            try {
                //服务端Socket监听8080端口
                serverSocket = new ServerSocket(8080);
                while (true) {
                    //阻塞状态等待并接收下一次socket连接，在客户端new Socket时会走到下一步，
                    //直到输入流读取的时候，如果没有输入信息，则会在下面in.read那里一直阻塞，会一直占用线程
                    Socket socket = serverSocket.accept();
                    //取得输入流
                    InputStream in = socket.getInputStream();

                    byte[] bytes = new byte[100];
                    while (in.read(bytes) > 0) {
                        String s = new String(bytes);
                        System.out.println(s);
                        for (byte readByte : bytes) {
                            System.out.println(readByte);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * IO客户端
     * 客户端使用Socket
     */
    private static class IOClient {

        public static void main(String[] args) {
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1", 8080);

                socket.getOutputStream().write(("now mills is:" + Instant.now().toEpochMilli()).getBytes());

                Thread.sleep(2000);
//
                socket.getOutputStream().write(("now mills is:" + Instant.now().toEpochMilli()).getBytes());

                System.out.println(1);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
