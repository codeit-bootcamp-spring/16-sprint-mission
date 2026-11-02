package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    static UserDto setupUser(UserService userService) {
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

    static ChannelDto setupChannel(ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "공지",
                "공지 채널입니다."
        );

        return channelService.createPublic(request);
    }

    static void messageCreateTest(
            MessageService messageService,
            ChannelDto channel,
            UserDto author
    ) {
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

        // Spring 실행 및 Context 가져오기
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        // Context에서 Bean 조회
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 현재 등록된 Repository 확인
        UserRepository userRepository = context.getBean(UserRepository.class);

        System.out.println("현재 Repository: "
                + userRepository.getClass().getName());

        // 현재 저장된 사용자 확인
        System.out.println("현재 저장된 사용자:");

        userRepository.findAll().forEach(user ->
                System.out.println(
                        "사용자 이름: " + user.getUsername()
                                + ", 이메일: " + user.getEmail()
                )
        );

        // 셋업
        UserDto user = setupUser(userService);
        ChannelDto channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}