package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DiscodeitApplication.class);

        app.addInitializers(applicationContext -> {
            Environment env = applicationContext.getEnvironment();
            boolean clearOnStart = env.getProperty("discodeit.repository.clear-data-on-start", Boolean.class, false);
            String fileDirectory = env.getProperty("discodeit.repository.file-directory", ".discodeit");

            if (clearOnStart) clearDataFiles(fileDirectory);
        });

        app.run(args);
    }

    public static void clearDataFiles(String directory) {
        Path baseDirectory = Path.of(directory);
        List<String> fileNames = List.of(
                "binary_content.ser",
                "channel.ser",
                "message.ser",
                "read_status.ser",
                "user.ser",
                "user_status.ser"
        );

        fileNames.forEach(fileName -> {
            Path filePath = baseDirectory.resolve(fileName);
            try {
                if (Files.deleteIfExists(filePath)) {
                    System.out.printf("파일 삭제: %s%n", filePath);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("파일 삭제 중 예외가 발생했습니다.", e);
            }
        });
    }
}
