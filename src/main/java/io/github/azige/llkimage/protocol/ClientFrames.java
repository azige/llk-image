/*
 * Created 2019-1-18 12:26:31
 */
package io.github.azige.llkimage.protocol;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.azige.llkimage.crypto.AesCipher;
import io.github.azige.llkimage.format.StoreImage;

/**
 *
 * @author Azige
 */
public final class ClientFrames {

    public static final Frame HELLO = new Frame(FrameConstants.CLOP_HELLO, FrameConstants.HELLO_MAGICS);
    public static final Frame GOOD_BYE = new Frame(FrameConstants.CLOP_GOOD_BYE, new byte[0]);

    public static Frame echo(String content) {
        return new Frame(FrameConstants.CLOP_ECHO, content.getBytes(Charset.forName("UTF-8")));
    }

    public static Frame upload(Path file, AesCipher cipher) throws IOException {
        StoreImage image = new StoreImage(file.getFileName().toString(), Files.readAllBytes(file));
        byte[] content = image.serialize();
        content = cipher.encrypt(content);
        return new Frame(FrameConstants.CLOP_UPLOAD, content);
    }

    public static Frame query(String imageHash) {
        return new Frame(FrameConstants.CLOP_QUERY, imageHash.getBytes());
    }

    public static Frame download(String imageHash) {
        return new Frame(FrameConstants.CLOP_DOWNLOAD, imageHash.getBytes());
    }

    private ClientFrames() {
    }
}
