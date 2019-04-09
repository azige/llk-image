/*
 * Created 2019-3-26 21:48:00
 */
package io.github.azige.llkimage.client;

import io.github.azige.llkimage.protocol.Frame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Azige
 */
public class ClientHandler extends SimpleChannelInboundHandler<Frame> {

    private BlockingQueue<Frame> receivedFrames = new LinkedBlockingQueue<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame msg) throws Exception {
        receivedFrames.offer(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    public Frame pollFrame() {
        return receivedFrames.poll();
    }

    public Frame blockingPollFrame() {
        try {
            return receivedFrames.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
