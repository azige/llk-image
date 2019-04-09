/*
 * Created 2019-1-23 18:48:05
 */
package io.github.azige.llkimage.format;

import java.nio.ByteBuffer;

/**
 *
 * @author Azige
 */
public final class Int24 {

    private Int24() {
    }

    public static void write(ByteBuffer buffer, int value) {
        buffer
            .put((byte) (value >> 16))
            .put((byte) (value >> 8))
            .put((byte) value);
    }

    public static int read(ByteBuffer buffer) {
        return (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff);
    }
}
