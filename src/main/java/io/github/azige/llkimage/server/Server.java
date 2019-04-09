/*
 * Created 2019-3-26 11:51:10
 */
package io.github.azige.llkimage.server;

import io.github.azige.llkimage.protocol.FrameCodec;

import java.io.IOException;
import java.nio.file.Paths;

import io.github.azige.llkimage.storage.Storage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private ServerContext serverContext;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public Server(ServerConfiguration config) throws IOException {
        serverContext = new ServerContext(
            new Storage(Paths.get(config.getStorageLocation()))
        );

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                            .addLast(new LengthFieldBasedFrameDecoder(100 << 20, 1, 3))
                            .addLast(new FrameCodec())
                            .addLast(new ServerHandler(serverContext));
                        LOG.info("收到来自 {} 的连接", ch.remoteAddress());
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        LOG.info("{} 已断开连接", ctx.channel().remoteAddress());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            channel = b.bind(config.getPort()).sync().channel();
            LOG.info("服务端已启动");
        } catch (Exception ex) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            throw new RuntimeException(ex);
        }
    }

    public void stop() {
        channel.close();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
            LOG.info("服务端已停止");
    }
}
