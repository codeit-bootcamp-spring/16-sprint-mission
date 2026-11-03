package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration(proxyBeanMethods = false)
public class RepositoryConfig {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    static class JCFRepositoryConfig {

        @Bean
        public UserRepository userRepository() {
            return new JCFUserRepository();
        }

        @Bean
        public ChannelRepository channelRepository() {
            return new JCFChannelRepository();
        }

        @Bean
        public MessageRepository messageRepository() {
            return new JCFMessageRepository();
        }

        @Bean
        public ReadStatusRepository readStatusRepository() {
            return new JCFReadStatusRepository();
        }

        @Bean
        public UserStatusRepository userStatusRepository() {
            return new JCFUserStatusRepository();
        }

        @Bean
        public BinaryContentRepository binaryContentRepository() {
            return new JCFBinaryContentRepository();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    static class FileRepositoryConfig {

        @Bean
        public UserRepository userRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileUserRepository(Path.of(directory));
        }

        @Bean
        public ChannelRepository channelRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileChannelRepository(Path.of(directory));
        }

        @Bean
        public MessageRepository messageRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileMessageRepository(Path.of(directory));
        }

        @Bean
        public ReadStatusRepository readStatusRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileReadStatusRepository(Path.of(directory));
        }

        @Bean
        public UserStatusRepository userStatusRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileUserStatusRepository(Path.of(directory));
        }

        @Bean
        public BinaryContentRepository binaryContentRepository(
                @Value("${discodeit.repository.file-directory:.discodeit}")
                String directory
        ) {
            return new FileBinaryContentRepository(Path.of(directory));
        }
    }
}