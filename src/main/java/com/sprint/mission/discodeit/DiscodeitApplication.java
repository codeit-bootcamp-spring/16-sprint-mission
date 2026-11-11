package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {
    static UserResponse setupUser(UserService userService) {
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "woody1234", null);
        return userService.create(request);
    }

    static UserResponse updateUserTest(UserService userService, UserResponse user) {
        // 변경하지 않을 필드는 null로 전달
        UpdateUserRequest request = new UpdateUserRequest("buzz", null, null, null);
        return userService.update(user.userId(), request);
    }

    static ChannelResponse setupChannel(ChannelService channelService) {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("공지", "공지 채널입니다.");
        return channelService.createPublicChannel(request);
    }

    static MessageResponse messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse author) {
        CreateMessageRequest request = new CreateMessageRequest("안녕하세요.", channel.id(), author.userId(), List.of());
        return messageService.create(request);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        ChannelService channelService = (ChannelService) context.getBean("basicChannelService");
        MessageService messageService = (MessageService) context.getBean("basicMessageService");
        UserService userService = (UserService) context.getBean("basicUserService");

        // 셋업
        UserResponse user = setupUser(userService);
        System.out.println("유저 생성: " + user);
        ChannelResponse channel = setupChannel(channelService);
        System.out.println("채널 생성: " + channel);

        // 테스트
        UserResponse updatedUser = updateUserTest(userService, user);
        System.out.println("유저 수정: " + updatedUser);
        MessageResponse message = messageCreateTest(messageService, channel, updatedUser);
        System.out.println("메시지 생성: " + message);
    }

}
