/*
 * Created 2019-2-12 15:46:31
 */
package io.github.azige.llkimage.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Azige
 */
public class Storage {

    private final Path storageDir;

    public Storage(Path storageDir) throws IOException {
        if (Files.exists(storageDir)) {
            if (!Files.isDirectory(storageDir)) {
                throw new IllegalStateException("指定的存储路径不是目录");
            }
        } else {
            Files.createDirectories(storageDir);
        }
        this.storageDir = storageDir;
    }

    public boolean exists(String hash) {
        Path file = storageDir.resolve(hash);
        return Files.exists(file);
    }

    public byte[] load(String hash) throws IOException {
        Path file = storageDir.resolve(hash);
        if (!Files.exists(file)) {
            return null;
        }
        return Files.readAllBytes(file);
    }

    public void save(String hash, byte[] content) throws IOException {
        Path file = storageDir.resolve(hash);
        Files.write(file, content);
    }
}
