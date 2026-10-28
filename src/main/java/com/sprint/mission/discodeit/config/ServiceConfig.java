package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository
    ) {
        return new BasicUserService(userRepository, channelRepository, messageRepository);
    }

    @Bean
    public ChannelService channelService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository
    ) {
        return new BasicChannelService(userRepository, channelRepository, messageRepository);
    }

    @Bean
    public MessageService messageService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository
    ) {
        return new BasicMessageService(userRepository, channelRepository, messageRepository);
    }
}
