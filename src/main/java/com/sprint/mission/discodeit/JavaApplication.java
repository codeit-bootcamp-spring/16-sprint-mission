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

        User user1 = new User("신지섭");
        User user2 = new User("아무나");

        userService.add(user1);
        userService.add(user2);

        System.out.println("추가 후 사용자 수: " + userService.getAll().size());

        userService.update(user1.getId(), "코드잇");

        System.out.println("수정 후 이름: " + userService.getById(user1.getId()).getUsername());

        userService.delete(user1.getId());

        System.out.println("삭제 후 사용자 수: " + userService.getAll().size());
        System.out.println("삭제 후 조회: " + userService.getById(user1.getId()));


        JCFChannelService channelService = new JCFChannelService();

        Channel channel = new Channel("자유게시판");

        channelService.add(channel);

        System.out.println("추가 후 채널 수: " + channelService.getAll().size());
        System.out.println("조회한 채널 이름: " + channelService.getById(channel.getId()).getName());

        channelService.update(channel.getId(), "아무나방");

        System.out.println("수정 후 채널 이름: " + channelService.getById(channel.getId()).getName());

        channelService.delete(channel.getId());

        System.out.println("삭제 후 채널 수: " + channelService.getAll().size());
        System.out.println("삭제 후 채널 조회: " + channelService.getById(channel.getId()));


        JCFMessageService messageService = new JCFMessageService();

        Channel messageChannel = new Channel("일반");
        channelService.add(messageChannel);

        Message message = new Message("안녕하세요!", user2.getId(), messageChannel.getId());

        messageService.add(message);

        System.out.println("추가 후 메시지 수: " + messageService.getAll().size());
        System.out.println("조회한 메시지: " + messageService.getById(message.getId()).getContent());

        messageService.update(message.getId(), "반갑습니다!");

        System.out.println("수정 후 메시지: " + messageService.getById(message.getId()).getContent());

        messageService.delete(message.getId());

        System.out.println("삭제 후 메시지 수: " + messageService.getAll().size());
        System.out.println("삭제 후 메시지 조회: " + messageService.getById(message.getId()));

    }
}