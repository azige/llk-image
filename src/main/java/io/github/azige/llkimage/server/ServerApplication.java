/*
 * Created 2019-2-12 15:26:59
 */
package io.github.azige.llkimage.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class ServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Path configPath = Paths.get("server-config.json");
        ServerConfiguration config;
        if (Files.exists(configPath)) {
            try {
                config = objectMapper.readValue(configPath.toFile(), ServerConfiguration.class);
            } catch (Exception ex) {
                LOG.warn("配置文件不正确，将使用默认配置", ex);
                config = new ServerConfiguration();
            }
        } else {
            config = new ServerConfiguration();
            try {
                objectMapper.writeValue(configPath.toFile(), config);
            } catch (Exception ex) {
                LOG.warn("无法创建配置文件");
            }
        }
        Server server = new Server(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));
    }
}
