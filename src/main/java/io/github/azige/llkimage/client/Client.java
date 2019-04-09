/*
 * Created 2019-3-26 21:34:09
 */
package io.github.azige.llkimage.client;

import java.net.SocketAddress;

import io.github.azige.llkimage.protocol.FrameCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author Azige
 */
public class Client {

    private EventLoopGroup workGroup;
    private ClientContext clientContext;
    private Bootstrap bootstrap;

    public Client(ClientContext clientContext) {
        this.clientContext = clientContext;

        workGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(100 << 20, 1, 3))
                        .addLast(new FrameCodec());
                }
            })
            .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public ClientConnection newConnection() {
        return newConnection(clientContext.getDefaultServerAddress());
    }

    public ClientConnection newConnection(SocketAddress serverAddress) {
        try {
            Channel channel = bootstrap.connect(serverAddress).sync().channel();
            return new ClientConnection(clientContext, channel);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() {
        workGroup.shutdownGracefully();
    }
}
