package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository(){
        return new FileUserRepository();
    }
    @Bean
    public ChannelRepository channelRepository(){
        return new FileChannelRepository();
    }
    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }
    @Bean
    public BinaryContentRepository binaryContentRepository(){return null;}
    @Bean
    public ReadStatusRepository readStatusRepository(){return null;}
    @Bean
    public UserStatusRepository userStatusRepository(){return null;}
}
