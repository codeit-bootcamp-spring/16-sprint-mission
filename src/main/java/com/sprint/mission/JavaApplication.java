package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.DiscordService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

//        // Service 생성
//        UserService userService = new JCFUserService();
//        ChannelService channelService =
//                new JCFChannelService(userService);
//        MessageService messageService =
//                new JCFMessageService(userService, channelService);
//        DiscordService discordService =
//                new DiscordService(userService, channelService, messageService);
        // Repository 생성
        UserRepository userRepository = new FileUserRepository();

        ChannelRepository channelRepository = new FileChannelRepository();

        MessageRepository messageRepository = new FileMessageRepository();


        // Service 생성
        UserService userService =
                new FileUserService(userRepository);

        ChannelService channelService =
                new FileChannelService(channelRepository, userService);

        MessageService messageService =
                new FileMessageService(messageRepository, userService, channelService);

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
                List.of(sehun.getId(), areum.getId(), jihun.getId())
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

        System.out.println("\n====== Channel 삭제 ======");
        channelService.deleteChannel(channel2.getId());
        System.out.println(channelService.findAll());

        // User <> Channel 관계 확인
        System.out.println("\n====== 세훈이 참여한 Channels ======");
        System.out.println(userService.findById(sehun.getId())
                .orElseThrow()
                .getChannels()
        );

        System.out.println("\n====== 1번 채팅방 참여 Users ======");
        System.out.println(channel1.getUsers());

        System.out.println("\n====== Channel 유저 추가 ======");
        System.out.println(channel1.getUsers());
        System.out.println("\n====== vvvvvvvvvvvvvvv ======");
        channelService.addUserToChannel(
                channel1.getId(),
                List.of(chulsu.getId())
        );
        System.out.println(channel1.getUsers());

        System.out.println("\n====== Channel 유저 삭제 ======");
        System.out.println(channel1.getUsers());
        System.out.println("\n====== vvvvvvvvvvvvvvv ======");
        channelService.removeUserFromChannel(
                channel1.getId(),
                List.of(chulsu.getId())
        );
        System.out.println(channel1.getUsers());

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
                "지훈의 첫번째 메세지",
                jihun.getId(),
                channel1.getId()
        );
        Message message4 = messageService.createMessage(
                "세훈의 두번째 메세지",
                sehun.getId(),
                channel1.getId()
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
        System.out.println(userService.findById(sehun.getId())
                .orElseThrow()
                .getMessages());

        System.out.println("\n====== 1번 채팅방 메세지 ======");
        System.out.println(channelService.findById(channel1.getId())
                .orElseThrow()
                .getMessages());

        System.out.println("\n====== Message 삭제 ======");
        messageService.deleteMessage(message2.getId());

        System.out.println("전체 메세지");
        System.out.println(messageService.findAll());

        System.out.println("아름의 메세지");
        System.out.println(areum.getMessages());

        System.out.println("1번 채팅방 메세지");
        System.out.println(channel1.getMessages());

        // 예외 처리 케이스
        UUID testUUID = UUID.randomUUID();
        System.out.println("\n====== 유저 이름 공백 ======");
        try {
            userService.createUser(" ", 20);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 유저 나이 음수 ======");
        try {
            userService.createUser("테스트", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 유저 수정 ======");
        try {
            userService.updateUser(testUUID, "몰라", 1);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 유저 삭제 ======");
        try {
            userService.updateUser(testUUID, "몰라", 1);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 채널 이름 공백 생성 ======");
        try {
            channelService.createChannel(" ", List.of(sehun.getId()));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 채널 참여자 없이 생성 ======");
        try {
            channelService.createChannel("테스트 채널", List.of());
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 앟는 유저로 채널 생성 ======");
        try {
            channelService.createChannel("테스트 채널", List.of(testUUID));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 중복되는 유저로 채널 생성 ======");
        try {
            channelService.createChannel("테스트 채널", List.of(sehun.getId(), sehun.getId()));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 이미 존재하는 유저 채널 추가 ======");
        try {
            channelService.addUserToChannel(channel1.getId(), List.of(sehun.getId()));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 참여하지 않은 유저 제거 ======");
        try {
            channelService.removeUserFromChannel(channel1.getId(), List.of(chulsu.getId()));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 채널 내 마지막 유저 제거 ======");
        try {
            channelService.removeUserFromChannel(channel3.getId(), List.of(sehun.getId()));
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 채널 수정 ======");
        try {
            channelService.updateChannel(testUUID, "이걸로");
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 채널 삭제 ======");
        try {
            channelService.deleteChannel(testUUID);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }

        System.out.println("\n====== 메세지 내용 없이 생성 ======");
        try {
            messageService.createMessage(" ", sehun.getId(), channel1.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 유저 메세지 생성 ======");
        try {
            messageService.createMessage("abc", testUUID, channel1.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 유저 메세지 생성 ======");
        try {
            messageService.createMessage("abc", testUUID, channel1.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 유저 메세지 생성 ======");
        try {
            messageService.createMessage("abc", testUUID, channel1.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 채널 메세지 생성 ======");
        try {
            messageService.createMessage("abc", sehun.getId(), testUUID);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 메세지 수정 ======");
        try {
            messageService.updateMessage(testUUID, "가나다라");
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 메세지 공백으로 수정 ======");
        try {
            messageService.updateMessage(message1.getId(), " ");
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }
        System.out.println("\n====== 존재하지 않는 메세지 삭제 ======");
        try {
            messageService.deleteMessage(testUUID);
        } catch (IllegalArgumentException e) {
            System.out.println("경고 : " + e.getMessage());
        }


        // 연쇄 삭제 테스트
        System.out.println("\n====== 연쇄 삭제 테스트 ======");
        System.out.println("===============================");
        System.out.println("세훈 삭제 전");
        System.out.println("===============================");
        System.out.println("USERS : ");
        System.out.println(userService.findAll());
        System.out.println("CHANNELS : ");
        System.out.println(channelService.findAll());
        System.out.println("MESSAGES : ");
        System.out.println(messageService.findAll());

        discordService.deleteUser(sehun.getId());

        System.out.println("===============================");
        System.out.println("세훈 삭제 후");
        System.out.println("===============================");
        System.out.println("USERS : ");
        System.out.println(userService.findAll());
        System.out.println("CHANNELS : ");
        System.out.println(channelService.findAll());
        System.out.println("MESSAGES : ");
        System.out.println(messageService.findAll());
    }
}