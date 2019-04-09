/*
 * Created 2019-1-23 19:02:19
 */
package io.github.azige.llkimage.format;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Azige
 */
public class MessageDescriptor {

    private final Map<String, FieldDescriptor> fieldMap;

    public MessageDescriptor(LinkedHashMap<String, FieldDescriptor> fieldMap) {
        this.fieldMap = Collections.unmodifiableMap(new LinkedHashMap<>(fieldMap));
    }

    public byte[] serialize(Map<String, byte[]> map) {
        int totalLength = 0;
        List<Field> fields = new ArrayList<>();
        for (Entry<String, FieldDescriptor> entry : fieldMap.entrySet()) {
            String name = entry.getKey();
            FieldDescriptor descriptor = entry.getValue();
            byte[] data = map.get(name);
            if (data == null) {
                throw new IllegalArgumentException("缺少字段：" + name);
            }

            fields.add(new Field(descriptor, data));

            if (descriptor.isFixedLength()) {
                totalLength += descriptor.getLength();
            } else {
                totalLength += 3 + data.length;
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        fields.forEach(field -> {
            if (field.getDescriptor().isFixedLength()) {
                buffer.put(field.getData());
            } else {
                Int24.write(buffer, field.getData().length);
                buffer.put(field.getData());
            }
        });

        return buffer.array();
    }

    public Map<String, byte[]> deserialize(byte[] message) {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Map<String, byte[]> map = new HashMap<>();
        fieldMap.forEach((name, descriptor) -> {
            byte[] bytes;
            if (descriptor.isFixedLength()) {
                bytes = new byte[descriptor.getLength()];
                buffer.get(bytes);
            } else {
                int length = Int24.read(buffer);
                bytes = new byte[length];
                buffer.get(bytes);
            }
            map.put(name, bytes);
        });
        return map;
    }
}
