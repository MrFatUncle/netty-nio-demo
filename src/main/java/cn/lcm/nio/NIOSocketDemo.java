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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Set;

public class NIOSocketDemo {
    /**
     * NIO服务端
     */
    private static class NIOServer {

        public static void main(String[] args) {
            Selector selector = null;
            ServerSocketChannel channel = null;
            try {
                //开启Selector
                selector = Selector.open();
                //开启ServerSocketChannel（并绑定端口）、设置非阻塞、将channel注册到selector上
                channel = ServerSocketChannel.open();
                channel.socket().bind(new InetSocketAddress(8080));
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_ACCEPT);

                //Selector判断有无连接
                while (selector.select(1000) > 0) {
                    //有连接，则看selector有多少个连接，遍历连接，并看连接对应的Accessable、Readable、Writable等是否有效
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();

                    while (selectionKeys.iterator().hasNext()) {
                        SelectionKey key = selectionKeys.iterator().next();

                        if(key.isAcceptable()) {
                            //将SocketChannel注册到Selector上去
                        }
                        if(key.isReadable()) {
                            //读数据
                        }
                        if(key.isWritable()) {
                            //写数据
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (selector != null) {
                        selector.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
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

                socket.getOutputStream().write(("now mills is:" + Instant.now().get(ChronoField.NANO_OF_SECOND)).getBytes());
            } catch (IOException e) {
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
