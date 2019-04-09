/*
 * Created 2019-1-16 11:39:25
 */
package io.github.azige.llkimage.protocol;

import java.nio.charset.Charset;

import lombok.Value;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Azige
 */
@Value
public class Frame {

    private byte opCode;
    private byte[] content;

    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("OP Code: 0x").append(Hex.encodeHex(new byte[]{opCode})).append("\n")
            .append("Content Length: ").append(content.length).append("\n");
        if (content.length > 0) {
            sb.append("Content Hex: ").append(Hex.encodeHex(content)).append("\n");
//                .append("Content Text: ").append(new String(content, Charset.forName("UTF-8"))).append("\n");
        }
        return sb.toString();
    }
}
