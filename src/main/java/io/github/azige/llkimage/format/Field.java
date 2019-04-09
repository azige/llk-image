/*
 * Created 2019-1-23 19:23:16
 */
package io.github.azige.llkimage.format;

/**
 *
 * @author Azige
 */
import lombok.Value;

@Value
public class Field {

    private FieldDescriptor descriptor;
    private byte[] data;

    public Field(FieldDescriptor descriptor, byte[] data) {
        if (descriptor.isFixedLength()) {
            if (data.length != descriptor.getLength()) {
                throw new IllegalArgumentException("数据长度与描述符的长度不相等");
            }
        }

        this.descriptor = descriptor;
        this.data = data;
    }
}
