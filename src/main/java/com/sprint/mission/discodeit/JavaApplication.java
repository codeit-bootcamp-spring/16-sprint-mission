package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.Collections;

public class JavaApplication {
    static User setupUser(UserService userService) {
        UserCreateDto dto = new UserCreateDto(
                "woody",
                "woody@codeit.com",
                "woody1234",
                null, null, 0L, null
        );
        return userService.create(dto);
    }

    static Channel setupChannel(ChannelService channelService) {
        PublicChannelCreateDto dto = new PublicChannelCreateDto("공지", "공지 채널입니다.");
        return channelService.createPublicChannel(dto);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageCreateDto dto = new MessageCreateDto(
                "안녕하세요.",
                channel.getId(),
                author.getId(),
                Collections.emptyList()
        );
        Message message = messageService.create(dto);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        String testFileDir = ".discodeit";

        UserRepository userRepository = new FileUserRepository(testFileDir);
        ChannelRepository channelRepository = new FileChannelRepository(testFileDir);
        MessageRepository messageRepository = new FileMessageRepository(testFileDir);
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(testFileDir);
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository(testFileDir);
        UserStatusRepository userStatusRepository = new FileUserStatusRepository(testFileDir);

        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, messageRepository, readStatusRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }
}