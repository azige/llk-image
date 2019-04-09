/*
 * Created 2019-1-16 11:28:39
 */
package io.github.azige.llkimage.protocol;

/**
 *
 * @author Azige
 */
public final class FrameConstants {

    public static final byte [] HELLO_MAGICS = "LLK".getBytes();

    public static final byte CLOP_HELLO = 0x10;
    public static final byte CLOP_GOOD_BYE = 0x11;
    public static final byte CLOP_ECHO = 0x12;
    public static final byte CLOP_UPLOAD = 0x13;
    public static final byte CLOP_QUERY = 0x14;
    public static final byte CLOP_DOWNLOAD = 0x15;

    public static final byte SVOP_MESSAGE = (byte) 0x80;
    public static final byte SVOP_ACK = (byte) 0x81;
    public static final byte SVOP_IMAGE_META = (byte) 0x82;
    public static final byte SVOP_IMAGE = (byte) 0x83;

    private FrameConstants() {
    }
}
