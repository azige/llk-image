/*
 * Created 2019-4-9 18:47:18
 */
package io.github.azige.llkimage.client;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.github.azige.llkimage.format.StoreImage;
import io.github.azige.llkimage.protocol.ClientFrames;
import io.github.azige.llkimage.protocol.Frame;
import io.github.azige.llkimage.protocol.FrameConstants;
import io.github.azige.llkimage.storage.Storage;
import io.netty.channel.Channel;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author Azige
 */
public class ClientConnection {

    private ClientContext clientContext;
    private Channel channel;
    private ClientHandler clientHandler;

    public ClientConnection(ClientContext clientContext, Channel channel) {
        this.clientContext = clientContext;
        this.channel = channel;
        clientHandler = new ClientHandler();

        channel.pipeline().addLast(clientHandler);
        writeFrame(ClientFrames.HELLO);
        expectFrame(FrameConstants.SVOP_ACK);
    }

    public void writeFrame(Frame frame) {
        channel.writeAndFlush(frame);
    }

    public Frame readFrame() {
        return clientHandler.blockingPollFrame();
    }

    public Single<Frame> readFrameAsync() {
        return Single.fromCallable(this::readFrame)
            .subscribeOn(Schedulers.io());
    }

    public Frame expectFrame(byte opCode) {
        Frame frame = readFrame();
        if (frame.getOpCode() != opCode) {
            throw new IllegalStateException(String.format("期望接收帧 0x%02x，但得到了 0x%02x", opCode, frame.getOpCode()));
        }
        return frame;
    }

    public Single<Frame> expectFrameAsync(byte opCode) {
        return Single.<Frame>create(emitter -> {
            Frame frame = readFrame();
            if (frame.getOpCode() != opCode) {
                emitter.onError(new IllegalStateException(String.format("期望接收帧 0x%02x，但得到了 0x%02x", opCode, frame.getOpCode())));
            } else {
                emitter.onSuccess(frame);
            }
        })
            .subscribeOn(Schedulers.io());
    }

    public String uploadImage(List<File> filesToUpload) throws IOException {
        StringBuilder uploadedUrisBuilder = new StringBuilder();
        for (File file : filesToUpload) {
            writeFrame(ClientFrames.upload(file.toPath(), clientContext.getCipher()));
            Frame frame = expectFrame(FrameConstants.SVOP_MESSAGE);

            uploadedUrisBuilder.append("llk:").append(new String(frame.getContent())).append("\n");
        }

        return uploadedUrisBuilder.toString();
    }

    public StoreImage downloadImage(String uri) throws IOException {
        String imageHash;
        if (uri.startsWith("llk:")) {
            imageHash = uri.substring(4);
        } else {
            imageHash = uri;
        }

        byte[] imageData;
        Storage storage = clientContext.getStorage();
        if (storage.exists(imageHash)) {
            imageData = storage.load(imageHash);
        } else {
            writeFrame(ClientFrames.query(imageHash));
            Frame frame = expectFrame(FrameConstants.SVOP_IMAGE_META);
            if (frame.getContent()[0] == 0) {
                return null;
            } else {
                writeFrame(ClientFrames.download(imageHash));
                imageData = expectFrame(FrameConstants.SVOP_IMAGE).getContent();
                storage.save(imageHash, imageData);
            }
        }
        return StoreImage.deserialize(clientContext.getCipher().decrypt(imageData));
    }

    public void close() {
        writeFrame(ClientFrames.GOOD_BYE);
        expectFrame(FrameConstants.SVOP_ACK);
        channel.close();
    }
}
