package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // USER CRUD
        User user = userService.createUser("세훈", 26);
        User user2 = userService.createUser("아름", 24);

        System.out.println(userService.findAll());
        System.out.println(userService.findById(user.getId()));
        System.out.println(userService.findByName(user.getName()));

        userService.updateUser(user.getId(), "태풍", 25);
        System.out.println(userService.findAll());

        userService.deleteUser(user2.getId());
        System.out.println(userService.findAll());


        // CHANNEL CRUD
        Channel channel = channelService.createChannel("1번채팅방");
        Channel channel2 = channelService.createChannel("2번채팅방");
        Channel channel3 = channelService.createChannel("3번채팅방");

        System.out.println(channelService.findAll());
        System.out.println(channelService.findById(channel.getId()));
        System.out.println(channelService.findByName(channel2.getName()));

        channelService.updateChannel(channel.getId(), "100번채팅방");
        System.out.println(channelService.findAll());

        channelService.deleteChannel(channel2.getId());
        System.out.println(channelService.findAll());


        // MESSAGE CRUD
        Message message = messageService.createMessage("배고파요", user.getId(), channel.getId());
        System.out.println(messageService.findAll());

        messageService.updateMessage(message.getId(), "오늘 저녁 뭐 먹지");
        System.out.println(messageService.findMessageById(message.getId()));

        messageService.deleteMessage(message.getId());
        System.out.println(messageService.findAll());


    }
}