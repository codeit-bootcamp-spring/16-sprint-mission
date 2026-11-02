package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaApplication {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JavaApplication(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    // 사용자 생성
    UserDto setupUser() {
        UserCreateRequest request = new UserCreateRequest(
                "woody",
                "woody@codeit.com",
                "woody1234",
                null,
                null,
                null
        );

        return userService.create(request);
    }

    // 공개 채널 생성
    ChannelDto setupChannel() {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "공지",
                "공지 채널입니다."
        );

        return channelService.createPublic(request);
    }

    // 메시지 생성 테스트
    void messageCreateTest(ChannelDto channel, UserDto author) {
        MessageCreateRequest request = new MessageCreateRequest(
                channel.getId(),
                author.getId(),
                "안녕하세요.",
                null
        );

        MessageDto message = messageService.create(request);

        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }
}