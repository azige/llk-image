/*
 * Created 2019-1-17 16:32:10
 */
package io.github.azige.llkimage.protocol;

import java.nio.charset.Charset;

/**
 *
 * @author Azige
 */
public final class ServerFrames {

    public static final Frame ACK = new Frame(FrameConstants.SVOP_ACK, new byte[0]);

    public static Frame message(String content) {
        return new Frame(FrameConstants.SVOP_MESSAGE, content.getBytes(Charset.forName("UTF-8")));
    }

    public static Frame message(byte[] content) {
        return new Frame(FrameConstants.SVOP_MESSAGE, content);
    }

    public static Frame imageMeta(boolean existed) {
        byte[] content = new byte[1];
        if (existed) {
            content[0] = 1;
        }
        return new Frame(FrameConstants.SVOP_IMAGE_META, content);
    }

    public static Frame image(byte[] content) {
        return new Frame(FrameConstants.SVOP_IMAGE, content);
    }

    private ServerFrames() {
    }
}
