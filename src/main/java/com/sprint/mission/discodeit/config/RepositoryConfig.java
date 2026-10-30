package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileUserRepository(Path.of(path));
    }

    @Bean
    public ChannelRepository channelRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileChannelRepository(Path.of(path));
    }

    @Bean
    public MessageRepository messageRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileMessageRepository(Path.of(path));
    }

    @Bean
    public ReadStatusRepository readStatusRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileReadStatusRepository(Path.of(path));
    }

    @Bean
    public UserStatusRepository userStatusRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileUserStatusRepository(Path.of(path));
    }

    @Bean
    public BinaryContentRepository binaryContentRepository(
            @Value("${discodeit.storage.path}") String path
    ) {
        return new FileBinaryContentRepository(Path.of(path));
    }
}