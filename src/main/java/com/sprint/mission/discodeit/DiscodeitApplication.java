package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        clearDataFiles();

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
    }

    public static void clearDataFiles() {
        List<String> fileList = List.of(
                ".discodeit/binary_content.ser",
                ".discodeit/channel.ser",
                ".discodeit/message.ser",
                ".discodeit/read_status.ser",
                ".discodeit/user.ser",
                ".discodeit/user_status.ser"
        );

        fileList.forEach(filePath -> {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.printf("파일 삭제: %s\n", file.getName());
                }
            }
        });
    }
}
