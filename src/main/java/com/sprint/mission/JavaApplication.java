package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        userService.setChannelService(channelService);
        userService.setMessageService(messageService);

        channelService.setUserService(userService);
        channelService.setMessageService(messageService);

        messageService.setUserService(userService);
        messageService.setChannelService(channelService);

        /**
         *
         *
         *
         * User CRUD
         */

        User user1 = new User("user1", "user1**", "user1@example.com");
        User user2 = new User("user2", "user2!!", "user2@naver.com");
        User user3 = new User("user3", "user333", "user3@gmail.com");
        User user4 = new User("user4", "user4^^", "user4@hello.com");

        // 유저 생성
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);

        // 유저 조회
        System.out.println("\n유저 조회");
        System.out.println(userService.getUserById(user1.getId()));
        System.out.println(userService.getUserById(user2.getId()));
        System.out.println(userService.getUserById(user3.getId()) + "\n");
//        System.out.println(userService.getUserById(user4.getId())); // "존재하지 않는 사용자입니다." 예외 처리

        // 유저 정보 변경
        System.out.println("\n유저 정보 변경");
        userService.updateUsername(user3.getId(), "user333"); // 유저 이름 변경
        System.out.println(userService.getUserById(user3.getId()));

        userService.updateEmail(user1.getId(), "user1111@codeit.com"); // 이메일 변경
        System.out.println(userService.getUserById(user1.getId()) + "\n");

        userService.updatePassword(user2.getId(), "user2!!", "user2222!!"); // 비밀번호 변경
//        userService.updatePassword(user2.getId(), "user2!!", "user2222!!"); // "비밀번호가 일치하지 않습니다." 예외 처리

        // 이메일 삭제
        System.out.println("\n유저 이메일 삭제");
        userService.deleteEmail(user1.getId());
        System.out.println(userService.getUserById(user1.getId()));

        // 유저 삭제
        System.out.println("\n유저 삭제");
        userService.deleteUser(user1.getId(), "user1**");
//        System.out.println(userService.getUserById(user1.getId())); // "존재하지 않는 사용자입니다." 예외 처리

        /**
         *
         *
         *
         * Channel CRUD
         */

        // 채널 생성
        Channel channel1 = new Channel("코드잇 백엔드 16기", user2.getId());
        channelService.createChannel(channel1);

        Channel channel2 = new Channel("중학교 동창 모임", user2.getId());
        channelService.createChannel(channel2);

        Channel channel3 = new Channel("소프트웨어학과 22학번", user3.getId());
        channelService.createChannel(channel3);

//        Channel channel4 = new Channel("     ", user2.getId()); // "채널명을 작성해 주세요." 예외 처리

        // user4 객체는 생성했지만 userData 필드엔 저장하지 않았기 때문
//        Channel channel4 = new Channel("코드잇 백엔드 16기", user4.getId());
//        channelService.createChannel(channel4); // "존재하지 않는 사용자입니다." 예외 처리

        // 채널 조회
        System.out.println("\n채널 조회");
        System.out.println(channelService.getChannelById(channel1.getId()));
        System.out.println(channelService.getChannelById(channel2.getId()));
        System.out.println(channelService.getChannelById(channel3.getId()));
        System.out.println(channelService.getChannelsByCreatorId(user2.getId())); // 특정 유저가 만든 모든 채널 리스트로 조회

        // 채널 이름 수정
        System.out.println("\n채널 이름 수정");
        channelService.updateChannelName(channel2.getId(), "고등학교 동창 모임");
        System.out.println(channelService.getChannelById(channel2.getId()));

        // 채널 삭제
        System.out.println("\n채널 삭제");
        channelService.deleteChannel(channel2.getId());
//        System.out.println(channelService.getChannelById(channel2.getId()) + "\n"); // "해당 채널이 존재하지 않습니다." 예외 처리

        /**
         *
         *
         *
         * Message CRUD
         */

        // 메시지 생성
        Message message1 = new Message("애들아 시험 기간 파이팅 ^^", user2.getId(), channel3.getId());
        messageService.sendMessage(message1);

        Message message2 = new Message("공부 좀 해", user2.getId(), channel3.getId());
        messageService.sendMessage(message2);

        Message message3 = new Message("엉덩이가 무거워야 된대... 그렇다고 많이 먹진 말고", user2.getId(), channel3.getId());
        messageService.sendMessage(message3);

        Message message4 = new Message("코드잇 파이팅~!!", user3.getId(), channel1.getId());
        messageService.sendMessage(message4);

        Message message5 = new Message("우울하지 말고 우웅 하자", user2.getId(), channel1.getId());
        messageService.sendMessage(message5);

        // 메시지 조회
        System.out.println("\n메시지 조회");
        System.out.println(messageService.getMessageById(message1.getId())); // 특정 메시지 조회
        System.out.println(messageService.getMessagesByUserId(user2.getId())); // 특정 유저가 쓴 메시지 조회
        System.out.println(messageService.getMessagesByChannelId(channel1.getId())); // 특정 채널 메시지 조회

        // 메시지 수정
        System.out.println("\n메시지 수정");
        messageService.updateMessage(message3.getId(), "장난이고 사실 많이 먹어도 돼");
        System.out.println(messageService.getMessageById(message3.getId()));

        // 메시지 삭제
        System.out.println("\n메시지 삭제");
        messageService.deleteMessage(message2.getId());
//        System.out.println(messageService.getMessageById(message2.getId())); // "존재하지 않는 메시지입니다." 예외 처리

    }
}
