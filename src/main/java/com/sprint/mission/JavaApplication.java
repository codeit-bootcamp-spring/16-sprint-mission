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
                new DiscordService(userService, channelService, messageService);


        // USER
        System.out.println("=== User 생성 ===");
        User sehun = userService.createUser("세훈", 26);
        User areum = userService.createUser("아름", 24);
        User jihun = userService.createUser("지훈", 20);
        User chulsu = userService.createUser("철수", 5);

        System.out.println("\n====== User 생성 확인 ======");
        System.out.println(userService.findAll());

        System.out.println("\n====== User 단건 조회 ======");
        System.out.println(userService.findById(areum.getId()));
        System.out.println("\n====== User 이름으로 조회 ======");
        System.out.println(userService.findByName("지훈"));

        System.out.println("\n====== User 수정 ======");
        userService.updateUser(sehun.getId(), "박세훈", 27);
        System.out.println("\n====== User 수정 확인 ======");
        System.out.println(userService.findById(sehun.getId()));

        // Channel 생성
        System.out.println("\n====== Channel 생성 ======");
        Channel channel1 = channelService.createChannel(
                "1. 단체 채팅방",
                List.of(sehun.getId(), areum.getId(), jihun.getId(), chulsu.getId())
        );
        Channel channel2 = channelService.createChannel(
                "2. 세훈 아름 채팅방",
                List.of(sehun.getId(), areum.getId())
        );
        Channel channel3 = channelService.createChannel(
                "3. 세훈 혼자 채팅방",
                List.of(sehun.getId())
        );
        System.out.println(channelService.findAll());

        System.out.println("\n====== Channel 수정 ======");
        channelService.updateChannel(
                channel1.getId(),
                "1. 수정된 단체 채팅방"
        );
        System.out.println(channelService.findById(channel1.getId()));

        // User <> Channel 관계 확인
        System.out.println("\n====== 세훈이 참여한 채널 ======");
        System.out.println(userService.findById(sehun.getId())
                .orElseThrow()
                .getChannels()
        );

        System.out.println("\n====== 1번 채팅방 참여 User ======");
        System.out.println(channel1.getUsers()
        );

        System.out.println("\n====== Channel 우저 추가 ======");
        System.out.println(channel2.getUsers());
        System.out.println("\n====== vvvvvvvvvvvvvvv ======");
        channelService.addUserToChannel(
                channel2.getId(),
                List.of(chulsu.getId())
        );
        System.out.println(channel2.getUsers());

        System.out.println("\n====== Channel 유저 삭제 ======");
        System.out.println(channel2.getUsers());
        System.out.println("\n====== vvvvvvvvvvvvvvv ======");
        channelService.removeUserFromChannel(
                channel2.getId(),
                List.of(chulsu.getId())
        );
        System.out.println(channel2.getUsers());

        // 메세지
        System.out.println("\n====== Message 생성 ======");
        Message message1 = messageService.createMessage(
                "세훈의 첫번째 메세지",
                sehun.getId(),
                channel1.getId()
        );
        Message message2 = messageService.createMessage(
                "아름의 첫번째 메세지",
                areum.getId(),
                channel1.getId()
        );
        Message message3 = messageService.createMessage(
                "철수의 첫번째 메세지",
                chulsu.getId(),
                channel1.getId()
        );
        Message message4 = messageService.createMessage(
                "세훈의 두번째 메세지",
                sehun.getId(),
                channel2.getId()
        );
        Message message5 = messageService.createMessage(
                "세훈이가 세훈에게 메세지",
                sehun.getId(),
                channel3.getId()
        );
        System.out.println(messageService.findAll());

        System.out.println("\n====== Message 단건 조회 ======");
        System.out.println(messageService.findMessageById(message1.getId()));

        System.out.println("\n====== Message 수정 ======");
        messageService.updateMessage(
                message1.getId(),
                "세훈이의 수정된 메세지"
        );
        System.out.println(messageService.findMessageById(message1.getId()));

        // USER CHANNEL MESSAGE 연관관계
        System.out.println("\n====== 세훈의 메세지 ======");
        System.out.println(sehun.getMessages());

        System.out.println("\n====== 1번 채팅방 메세지 ======");
        System.out.println(channel1.getMessages());

        System.out.println("\n====== Message 삭제 ======");
        messageService.deleteMessage(message2.getId());

        System.out.println("전체 메세지");
        System.out.println(messageService.findAll());

        System.out.println("아름의 메세지");
        System.out.println(areum.getMessages());

        System.out.println("1번 채팅방 메세지");
        System.out.println(channel1.getMessages());

        // 연쇄 삭제 테스트
        System.out.println("=====================================");
        System.out.println("세훈 삭제 전");
        System.out.println("=====================================");
        System.out.println("USERS : ");
        System.out.println(userService.findAll());
        System.out.println("CHANNELS : ");
        System.out.println(channelService.findAll());
        System.out.println("MESSAGES : ");
        System.out.println(messageService.findAll());

        discordService.deleteUser(sehun.getId());

        System.out.println("=====================================");
        System.out.println("세훈 삭제 후");
        System.out.println("=====================================");
        System.out.println("USERS : ");
        System.out.println(userService.findAll());
        System.out.println("CHANNELS : ");
        System.out.println(channelService.findAll());
        System.out.println("MESSAGES : ");
        System.out.println(messageService.findAll());
    }
}