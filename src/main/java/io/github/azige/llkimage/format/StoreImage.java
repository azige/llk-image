/*
 * Created 2019-1-23 20:06:47
 */
package io.github.azige.llkimage.format;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class StoreImage {

    private static final MessageDescriptor MESSAGE_DESCRIPTOR = createMessageDescriptor();
    private String fileName;
    private byte[] data;

    public byte[] serialize() {
        Map<String, byte[]> map = new HashMap<>();
        map.put("fileName", fileName.getBytes(Charset.forName("UTF-8")));
        map.put("data", data);
        return MESSAGE_DESCRIPTOR.serialize(map);
    }

    public static StoreImage deserialize(byte[] message) {
        Map<String, byte[]> map = MESSAGE_DESCRIPTOR.deserialize(message);
        String fileName = new String(map.get("fileName"), Charset.forName("UTF-8"));
        byte[] data = map.get("data");
        return new StoreImage(fileName, data);
    }

    private static MessageDescriptor createMessageDescriptor() {
        LinkedHashMap<String, FieldDescriptor> map = new LinkedHashMap<>();
        map.put("fileName", FieldDescriptor.variable());
        map.put("data", FieldDescriptor.variable());
        return new MessageDescriptor(map);
    }
}
