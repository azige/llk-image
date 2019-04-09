/*
 * Created 2019-1-23 19:02:33
 */
package io.github.azige.llkimage.format;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldDescriptor {

    private static final FieldDescriptor VARIABLE = new FieldDescriptor(false, 0);

    private boolean fixedLength;
    private int length;

    public static FieldDescriptor fixed(int length) {
        return new FieldDescriptor(true, length);
    }

    public static FieldDescriptor variable() {
        return VARIABLE;
    }
}
