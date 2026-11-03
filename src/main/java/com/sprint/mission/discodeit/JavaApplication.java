package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
    static UserResponseDto setupUser(UserService userService) {
        UserResponseDto user = userService.create(
                new UserCreateRequest("woody", "woody@codeit.com", "woody1234", null));
        return user;
    }

    static ChannelResponseDto setupChannel(ChannelService channelService) {
        ChannelResponseDto channel = channelService.createPublic(
                new PublicChannelCreateRequest("공지", "공지 채널입니다."));
        return channel;
    }

    static void messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
        Message message = messageService.create(
                new MessageCreateRequest("안녕하세요.", channel.id(), author.id(), null));
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository();
        UserStatusRepository userStatusRepository = new FileUserStatusRepository();
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository();

        // 매퍼 초기화
        UserMapper userMapper = new UserMapper(userStatusRepository);

        // 서비스 초기화
        UserService userService = new BasicUserService(
                userRepository, binaryContentRepository, userStatusRepository, userMapper);
        ChannelService channelService = new BasicChannelService(
                channelRepository, readStatusRepository, messageRepository, binaryContentRepository);
        MessageService messageService = new BasicMessageService(
                messageRepository, channelRepository, userRepository, binaryContentRepository);

        // 셋업
        UserResponseDto user = setupUser(userService);
        ChannelResponseDto channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}