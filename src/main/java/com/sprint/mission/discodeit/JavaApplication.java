package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaApplication implements CommandLineRunner {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    // 생성자를 통해 Spring이 서비스 객체를 자동으로 전달
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
    User setupUser() {
        User user = userService.create(
                "woody",
                "woody@codeit.com",
                "woody1234"
        );
        return user;
    }

    // 채널 생성
    Channel setupChannel() {
        Channel channel = channelService.create(
                ChannelType.PUBLIC,
                "공지",
                "공지 채널입니다."
        );
        return channel;
    }

    // 메시지 생성 테스트
    void messageCreateTest(Channel channel, User author) {
        Message message = messageService.create(
                "안녕하세요.",
                channel.getId(),
                author.getId()
        );

        System.out.println("메시지 생성: " + message.getId());
    }

    // Spring이 시작된 후 실행되는 메서드
    @Override
    public void run(String... args) {
        User user = setupUser();
        Channel channel = setupChannel();

        messageCreateTest(channel, user);
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }
}

