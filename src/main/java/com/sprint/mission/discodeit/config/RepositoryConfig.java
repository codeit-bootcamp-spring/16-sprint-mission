package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public UserRepository jcfUserRepository() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public ChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public MessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public BinaryContentRepository jcfBinaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public ReadStatusRepository jcfReadStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public UserStatusRepository jcfUserStatusRepository() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public UserRepository fileUserRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileUserRepository(fileDir);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public ChannelRepository fileChannelRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileChannelRepository(fileDir);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public MessageRepository fileMessageRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileMessageRepository(fileDir);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public BinaryContentRepository fileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileBinaryContentRepository(fileDir);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public ReadStatusRepository fileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileReadStatusRepository(fileDir);
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
    public UserStatusRepository fileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDir) {
        return new FileUserStatusRepository(fileDir);
    }
}
