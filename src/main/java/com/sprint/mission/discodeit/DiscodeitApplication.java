package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
    // 여긴 다 직접 변경한 서비스를 빈으로 호출할 수 있도록 변경하고
    // 각 새로만든 dto를 활용할 수 있는 코드로 변경해주세요.

    // static 메서드는 클래스에 속해있지 않기 때문에 클래스의 변수를 참조할 수 없습니다.
    static User setupUser(UserService userService) {
        UserCreateRequest userCreateRequest = new UserCreateRequest("woody",
                "woody@codeit.com", "woody1234", null);
        // User user = userService.create("woody", "woody@codeit.com", "woody1234");
        User user = userService.create(userCreateRequest);
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("ch01", "channel 01");
        // Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        Channel channel = channelService.createPublicChannel(publicChannelCreateRequest);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId(), null);
        // Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        Message message = messageService.create(messageCreateRequest);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 빈 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
