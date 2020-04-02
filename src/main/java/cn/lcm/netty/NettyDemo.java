/**
 * FileName: NettyDemo
 * Author:   liaocm
 * Date:     2020/4/1 17:36
 * Description:
 * History:
 */
package cn.lcm.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.time.LocalDateTime;

public class NettyDemo {

    private static class NettyServer {
        public static void main(String[] args) {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup();
            serverBootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    System.out.println(msg);
                                }
                            });
                        }
                    })
                    .bind(8000);
        }
    }

    private static class NettyClient {
        public static void main(String[] args) throws InterruptedException {
            Bootstrap bootstrap = new Bootstrap();
            NioEventLoopGroup group = new NioEventLoopGroup();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();

            while (true) {
                channel.writeAndFlush(LocalDateTime.now() + ": hello world!");
                Thread.sleep(2000);
            }
        }
    }
}
