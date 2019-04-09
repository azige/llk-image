/*
 * Created 2019-3-26 16:18:53
 */
package io.github.azige.llkimage.server;

import java.io.IOException;

import io.github.azige.llkimage.protocol.Frame;
import io.github.azige.llkimage.protocol.FrameConstants;
import io.github.azige.llkimage.protocol.ServerFrames;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Azige
 */
public class ServerHandler extends SimpleChannelInboundHandler<Frame> {

    private enum State {
        INIT, ESTABLISHED, CLOSED;
    }

    private final ServerContext serverContext;
    private State state = State.INIT;
    private boolean _notAcceptable;

    public ServerHandler(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame msg) throws Exception {
        _notAcceptable = false;
        switch (state) {
            case INIT:
                handleInit(ctx, msg);
                break;
            case ESTABLISHED:
                handleEstablished(ctx, msg);
                break;
            case CLOSED:
                handleClosed(ctx, msg);
                break;
            default:
                throw new AssertionError();
        }
        if (_notAcceptable) {
            ctx.write(ServerFrames.message(String.format("不可接受的帧，opCode=0x%02x，state=%s", msg.getOpCode(), state)));
        }
        ctx.flush();
    }

    private void handleInit(ChannelHandlerContext ctx, Frame msg) {
        if (msg.getOpCode() == FrameConstants.CLOP_HELLO) {
            ctx.write(ServerFrames.ACK);
            state = State.ESTABLISHED;
        } else {
            notAcceptable();
        }
    }

    private void handleEstablished(ChannelHandlerContext ctx, Frame msg) throws IOException {
        switch (msg.getOpCode()) {
            case FrameConstants.CLOP_ECHO:
                ctx.write(ServerFrames.message(msg.getContent()));
                break;
            case FrameConstants.CLOP_GOOD_BYE:
                ctx.write(ServerFrames.ACK).addListener(ChannelFutureListener.CLOSE);
                state = State.CLOSED;
                break;
            case FrameConstants.CLOP_UPLOAD: {
                String hash = Hex.encodeHexString(DigestUtils.sha1(msg.getContent()));
                serverContext.getStorage().save(hash, msg.getContent());
                ctx.write(ServerFrames.message(hash));
                break;
            }
            case FrameConstants.CLOP_QUERY:
                ctx.write(ServerFrames.imageMeta(serverContext.getStorage().exists(new String(msg.getContent()))));
                break;
            case FrameConstants.CLOP_DOWNLOAD: {
                String hash = new String(msg.getContent());
                if (serverContext.getStorage().exists(hash)) {
                    ctx.write(ServerFrames.image(serverContext.getStorage().load(hash)));
                } else {
                    ctx.write(ServerFrames.message("资源不存在"));
                }
                break;
            }
            default:
                notAcceptable();
        }
    }

    private void handleClosed(ChannelHandlerContext ctx, Frame msg) {
        notAcceptable();
    }

    private void notAcceptable() {
        _notAcceptable = true;
    }
}
