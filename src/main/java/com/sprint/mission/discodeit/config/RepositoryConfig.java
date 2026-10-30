package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public UserRepository jcfUserRepository() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public ChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public MessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }


    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public BinaryContentRepository jcfBinaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public ReadStatusRepository jcfReadStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "jcf")
    public UserStatusRepository jcfUserStatusRepository() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public UserRepository fileUserRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileUserRepository(Path.of(fileDirectory, "users"));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public ChannelRepository fileChannelRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileChannelRepository(Path.of(fileDirectory, "channels"));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public MessageRepository fileMessageRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileMessageRepository(Path.of(fileDirectory, "messages"));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public BinaryContentRepository fileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileBinaryContentRepository(Path.of(fileDirectory, "binary-contents"));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public ReadStatusRepository fileReadStatusRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileReadStatusRepository(Path.of(fileDirectory, "read-statuses"));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository", name = "type", havingValue = "file")
    public UserStatusRepository fileUserStatusRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        return new FileUserStatusRepository(Path.of(fileDirectory, "user-statuses"));
    }

}
