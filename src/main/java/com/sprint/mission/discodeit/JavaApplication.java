package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

public class JavaApplication {

    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();

        User user1 = userService.add(new User("신지섭"));
        User user2 = userService.add(new User("아무나"));

        System.out.println("추가 후 사용자 수: " + userService.getAll().size());

        System.out.println("조회한 사용자: " + userService.getById(user1.getId()).getUsername());

        User updatedUser = userService.update(user1.getId(), "코드잇");

        System.out.println("수정 후 이름: " + updatedUser.getUsername());

        userService.delete(user1.getId());

        System.out.println("삭제 후 사용자 수: " + userService.getAll().size());

        try {
            userService.getById(user1.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("사용자 삭제 확인: " + e.getMessage());
        }


        JCFChannelService channelService = new JCFChannelService();

        Channel channel = channelService.add(new Channel("자유게시판"));

        System.out.println("추가 후 채널 수: " + channelService.getAll().size());

        System.out.println("조회한 채널 이름: " + channelService.getById(channel.getId()).getName());

        Channel updatedChannel = channelService.update(channel.getId(), "잡담방");

        System.out.println("수정 후 채널 이름: " + updatedChannel.getName());

        channelService.delete(channel.getId());

        System.out.println("삭제 후 채널 수: " + channelService.getAll().size());

        try {
            channelService.getById(channel.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("채널 삭제 확인: " + e.getMessage());
        }


        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        Channel messageChannel = channelService.add(new Channel("일반"));

        Message message = messageService.add(
                new Message("안녕하세요!", user2.getId(), messageChannel.getId())
        );

        System.out.println("추가 후 메시지 수: " + messageService.getAll().size());

        System.out.println("조회한 메시지: " + messageService.getById(message.getId()).getContent());

        Message updatedMessage = messageService.update(message.getId(), "반갑습니다!");

        System.out.println("수정 후 메시지: " + updatedMessage.getContent());

        messageService.delete(message.getId());

        System.out.println("삭제 후 메시지 수: " + messageService.getAll().size());

        try {
            messageService.getById(message.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("메시지 삭제 확인: " + e.getMessage());
        }


        try {
            Message message1 = new Message("테스트1", user1.getId(), messageChannel.getId());
            messageService.add(message1);
            System.out.println("사용자 검증 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("사용자 검증 성공: " + e.getMessage());
        }

        try {
            Message message2 = new Message("테스트2", user2.getId(), channel.getId());
            messageService.add(message2);
            System.out.println("채널 검증 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 검증 성공: " + e.getMessage());
        }

        System.out.println("최종 메시지 수: " + messageService.getAll().size());
    }
}