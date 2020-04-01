/**
 * FileName: NIODemo
 * Author:   liaocm
 * Date:     2020/4/1 17:36
 * Description:
 * History:
 */
package cn.lcm.nio;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * 这个类展示NIO读取文件的方式：
 * 1.NIO概念，面向Channel与Buffer，在Socket通信中由Selector进行管理
 */
public class NIODemo {

    public static void main(String[] args) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile("D:/git_project/my.txt", "rw");
            //获取channel
            FileChannel channel = randomAccessFile.getChannel();
            //分配缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //从channel读取到缓冲区，根据缓冲区大小依次读取
            while (channel.read(buffer) > 0) {
                //重置缓冲区
                buffer.flip();
                //看buffer的position属性是否已超过limit
                while (buffer.hasRemaining()) {
                    //获取下一个位置的数据
                    byte b = buffer.get();
                    System.out.println(b);
                }
                //
                buffer.compact();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 这个类展示传统BIO读取文件的常见方式
 */
class IODemo {

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("D:/git_project/my.txt");

        InputStream in = new FileInputStream(file);
        byte[] bytes = new byte[1024];

        try {
            while (in.read(bytes) > 0) {
                for (byte readByte : bytes) {
                    System.out.println(readByte);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
