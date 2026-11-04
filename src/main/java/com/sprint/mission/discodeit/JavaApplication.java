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
        //모든 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository();
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository();
        UserStatusRepository userStatusRepository = new FileUserStatusRepository();

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, messageRepository, readStatusRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);

        //셋업 및 동작 테스트
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }
}
