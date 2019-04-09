/*
 * Created 2019-3-26 15:47:58
 */
package io.github.azige.llkimage.protocol;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 *
 * @author Azige
 */
public class FrameCodec extends ByteToMessageCodec<Frame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte opCode = in.readByte();
        int contentLength = in.readUnsignedMedium();
        byte[] content = new byte[contentLength];
        in.readBytes(content);
        out.add(new Frame(opCode, content));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getOpCode());
        out.writeMedium(msg.getContent().length);
        out.writeBytes(msg.getContent());
    }

}
