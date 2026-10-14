package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.DiscordService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {

    public static void main(String[] args) {

        // Service 생성
        UserService userService = new JCFUserService();

        ChannelService channelService =
                new JCFChannelService(userService);

        MessageService messageService =
                new JCFMessageService(userService, channelService);

        DiscordService discordService =
                new DiscordService(
                        userService,
                        channelService,
                        messageService
                );


        // 1. User 생성
        User user = userService.createUser("세훈", 26);
        User user2 = userService.createUser("아름", 24);
        User user3 = userService.createUser("지훈", 20);


        // 2. Channel 생성
        // 세훈 + 아름 + 지훈
        Channel channel = channelService.createChannel(
                "1번채팅방",
                List.of(
                        user.getId(),
                        user2.getId(),
                        user3.getId()
                )
        );

        // 세훈 + 아름
        Channel channel2 = channelService.createChannel(
                "2번채팅방",
                List.of(
                        user.getId(),
                        user2.getId()
                )
        );

        // 세훈 혼자
        // → 세훈 삭제 시 이 채널 자체가 삭제되어야 함
        Channel channel3 = channelService.createChannel(
                "세훈혼자채팅방",
                List.of(user.getId())
        );

        // 3. Message 생성

        // 세훈이 1번 채팅방에 작성
        messageService.createMessage(
                "세훈 메시지 1",
                user.getId(),
                channel.getId()
        );

        // 아름이 1번 채팅방에 작성
        messageService.createMessage(
                "아름 메시지",
                user2.getId(),
                channel.getId()
        );

        // 세훈이 2번 채팅방에 작성
        messageService.createMessage(
                "세훈 메시지 2",
                user.getId(),
                channel2.getId()
        );

        // 세훈 혼자 있는 채팅방에 작성
        messageService.createMessage(
                "세훈 혼자 작성한 메시지",
                user.getId(),
                channel3.getId()
        );

        // 삭제 전 상태
        System.out.println("========== 세훈 삭제 전 ==========");

        System.out.println("User 목록");
        System.out.println(userService.findAll());

        System.out.println("\nChannel 목록");
        System.out.println(channelService.findAll());

        System.out.println("\n1번 채팅방 User");
        System.out.println(channel.getUsers());

        System.out.println("\n2번 채팅방 User");
        System.out.println(channel2.getUsers());

        System.out.println("\nMessage 목록");
        System.out.println(messageService.findAll());

        // 4. 세훈 삭제
        System.out.println("\n\n========== 세훈 삭제 ==========");

        discordService.deleteUser(user.getId());

        // 삭제 후 상태
        System.out.println("\n========== 세훈 삭제 후 ==========");

        System.out.println("User 목록");
        System.out.println(userService.findAll());

        System.out.println("\nChannel 목록");
        System.out.println(channelService.findAll());

        System.out.println("\n1번 채팅방 User");
        System.out.println(channel.getUsers());

        System.out.println("\n2번 채팅방 User");
        System.out.println(channel2.getUsers());

        System.out.println("\nMessage 목록");
        System.out.println(messageService.findAll());
    }
}