package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
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

}
