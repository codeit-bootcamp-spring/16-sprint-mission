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
         * User
         */

        System.out.println("==========================[User]==========================");

        User user1 = new User("user1", "user1**", "user1@example.com");
        User user2 = new User("user2", "user2!!", "user2@naver.com");
        User user3 = new User("user3", "user333", "user3@gmail.com");

        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);

        // 조회
        System.out.println(userService.getUserById(user1.getId()));
        System.out.println(userService.getUserById(user2.getId()));

        // 수정
        userService.updateUsername(user3.getId(), "user3333");
        System.out.println("이름 변경: " + userService.getUserById(user3.getId()));

        userService.updateEmail(user3.getId(), "user3333@gmail.com");
        System.out.println("이메일 변경: " + userService.getUserById(user3.getId()));

        userService.updatePassword(user2.getId(), "user2!!", "user2222!!");
        System.out.println("비밀번호 변경 완료");

        // 예외 확인
        try {
            userService.updateEmail(user3.getId(), "user1@example.com");
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        try {
            userService.updatePassword(user2.getId(), "pwd1234", "new1234!!");
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }


        /**
         *
         *
         *
         * Channel
         */

        System.out.println("\n==========================[Channel]==========================");

        Channel channel1 = new Channel("코드잇 백엔드 16기", user1.getId());
        Channel channel2 = new Channel("중학교 동창 모임", user2.getId());
        channelService.createChannel(channel1);
        channelService.createChannel(channel2);

        // 입장
        channelService.joinChannel(channel1.getId(), user2.getId());
        channelService.joinChannel(channel1.getId(), user3.getId());
        System.out.println("\"" + channelService.getChannelById(channel1.getId()).getChannelName() +
                "\" 참여자 수: " + channelService.getParticipants(channel1.getId()).size() + "명");
        System.out.println("\"" + channelService.getChannelById(channel1.getId()).getChannelName() +
                "\" 참여자 목록: " + channelService.getParticipants(channel1.getId()).stream()
                .map(User::getUsername).toList());

        // 조회
        System.out.println(channelService.getChannelById(channel1.getId()));
        System.out.println(userService.getUserById(channel1.getCreatorId()).getUsername() +
                "이 만든 채널: " + channelService.getChannelsByCreatorId(user1.getId()));

        // 수정
        System.out.println("채널 이름 변경 전: " + channelService.getChannelById(channel2.getId()).getChannelName());
        channelService.updateChannelName(channel2.getId(), user2.getId(), "고등학교 동창 모임");
        System.out.println("채널 이름 변경 후: " + channelService.getChannelById(channel2.getId()).getChannelName());

        try {
            channelService.createChannel(new Channel("코드잇 백엔드 16기", user2.getId()));
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        try {
            channelService.updateChannelName(channel1.getId(), user2.getId(), "내가 바꿀래");
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        try {
            channelService.joinChannel(channel1.getId(), user2.getId());
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        /**
         *
         *
         *
         * Message
         */

        System.out.println("\n==========================[Message]==========================");

        Message message1 = new Message("안녕하세요!", user1.getId(), channel1.getId());
        Message message2 = new Message("반갑습니다~", user2.getId(), channel1.getId());
        Message message3 = new Message("코드잇 파이팅", user3.getId(), channel1.getId());
        messageService.createMessage(message1);
        messageService.createMessage(message2);
        messageService.createMessage(message3);

        // 조회
        User user2Info = userService.getUserById(user2.getId());
        System.out.println(user2Info.getUsername() + "가 쓴 메시지:");
        for (Message m : messageService.getMessagesByUserId(user2.getId())) {
            Channel c = channelService.getChannelById(m.getChannelId());
            System.out.println("  [" + c.getChannelName() + "] " + m.getContent());
        }

        Channel channelInfo = channelService.getChannelById(channel1.getId());
        System.out.println("\n[" + channelInfo.getChannelName() + "] 채널 메시지:");
        for (Message m : messageService.getMessagesByChannelId(channel1.getId())) {
            User sender = userService.getUserById(m.getSenderId());
            System.out.println("  " + sender.getUsername() + ": " + m.getContent());
        }

        // 수정
        messageService.updateMessage(message2.getId(), user2.getId(), "반갑습니다람쥐!!🐿️🐿️");
        System.out.println("\n메시지 수정 후 [" + channelInfo.getChannelName() + "] 채널 메시지:");
        for (Message m : messageService.getMessagesByChannelId(channel1.getId())) {
            User sender = userService.getUserById(m.getSenderId());
            System.out.println("  " + sender.getUsername() + ": " + m.getContent());
        }

        try {
            messageService.updateMessage(message1.getId(), user2.getId(), "남의 메시지 수정");
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        try {
            Message message4 = new Message("참여 안 한 채널", user3.getId(), channel2.getId());
            messageService.createMessage(message4);
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        // 삭제
        messageService.deleteMessage(message3.getId(), user3.getId());
        System.out.println("\n메시지 삭제 후 [" + channelInfo.getChannelName() + "] 채널 메시지:");
        for (Message m : messageService.getMessagesByChannelId(channel1.getId())) {
            User sender = userService.getUserById(m.getSenderId());
            System.out.println("  " + sender.getUsername() + ": " + m.getContent());
        }

        // 퇴장
        channelService.leaveChannel(channel1.getId(), user3.getId());
        System.out.println("\n퇴장 후 참여자 수: " + channelService.getParticipants(channel1.getId()).size());
        System.out.println("\n퇴장 후 [" + channelInfo.getChannelName() + "] 채널 메시지:");
        for (Message m : messageService.getMessagesByChannelId(channel1.getId())) {
            User sender = userService.getUserById(m.getSenderId());
            System.out.println("  " + sender.getUsername() + ": " + m.getContent());
        }

        try {
            channelService.leaveChannel(channel1.getId(), user1.getId());
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        /**
         *
         *
         *
         *
         */

        System.out.println("\n<< 연쇄 삭제 >>");

        // 유저 삭제 (user2: channel2 방장, channel1 참여자)
        userService.deleteUser(user2.getId(), "user2222!!");
        System.out.println("user2 유저 삭제 후 [" + channelInfo.getChannelName() + "] 채널 메시지:");
        for (Message m : messageService.getMessagesByChannelId(channel1.getId())) {
            User sender = userService.getUserById(m.getSenderId());
            System.out.println("  " + sender.getUsername() + ": " + m.getContent());
        }

        try {
            channelService.getChannelById(channel2.getId());
        } catch (Exception e) {
            System.out.println("예외: " + e.getMessage());
        }

        // 채널 삭제 (channel1)
        Channel channel3 = new Channel("user1의 개인 채널", user1.getId());
        channelService.createChannel(channel3);

        Message message5 = new Message("여기는 나만의 채널", user1.getId(), channel3.getId());
        Message message6 = new Message("채널1이 지워져도 이건 남아야 함", user1.getId(), channel3.getId());
        messageService.createMessage(message5);
        messageService.createMessage(message6);

        // 채널 삭제 (channel1)
        User user1Info = userService.getUserById(user1.getId());
        System.out.println("\n채널 삭제 전 " + user1Info.getUsername() + "가 쓴 메시지:");
        for (Message m : messageService.getMessagesByUserId(user1.getId())) {
            Channel c = channelService.getChannelById(m.getChannelId());
            System.out.println("  [" + c.getChannelName() + "] " + m.getContent());
        }

        channelService.deleteChannel(channel1.getId(), user1.getId());

        try {
            channelService.getChannelById(channel1.getId());
        } catch (Exception e) {
            System.out.println("예외: " + e.getMessage());
        }

        System.out.println("\n채널 삭제 후 " + user1Info.getUsername() + "가 쓴 메시지:");
        for (Message m : messageService.getMessagesByUserId(user1.getId())) {
            Channel c = channelService.getChannelById(m.getChannelId());
            System.out.println("  [" + c.getChannelName() + "] " + m.getContent());
        }
    }
}