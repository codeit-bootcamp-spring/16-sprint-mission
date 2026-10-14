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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);

        // USER CRUD
        User user = userService.createUser("세훈", 26);
        User user2 = userService.createUser("아름", 24);
        User user3 = userService.createUser("지훈", 20);

//        System.out.println(userService.findAll());
//        System.out.println(userService.findById(user.getId()));
//        System.out.println(userService.findByName(user.getName()));

//        userService.updateUser(user.getId(), "태풍", 25);
//        System.out.println(userService.findAll());

//        userService.deleteUser(user2.getId());
//        System.out.println(userService.findAll());

        // CHANNEL CRUD
        List<UUID> userIds = new ArrayList<>();
        userIds.add(user.getId());
        userIds.add(user2.getId());
        userIds.add(user3.getId());

        Channel channel = channelService.createChannel("1번채팅방", userIds);
        Channel channel2 = channelService.createChannel("2번채팅방", userIds);
        Channel channel3 = channelService.createChannel("3번채팅방", userIds);

//        System.out.println(channelService.findAll());
//        System.out.println(channelService.findById(channel.getId()));
//        System.out.println(channelService.findByName(channel2.getName()));

//        channelService.updateChannel(channel.getId(), "100번채팅방");
//        System.out.println(channelService.findAll());

//        channelService.deleteChannel(channel2.getId());
//        System.out.println(channelService.findAll());

        List<UUID> userIds1 = new ArrayList<>();
        userIds1.add(user.getId());
        channelService.removeUserFromChannel(channel.getId(), userIds1);
        System.out.println(channel.getUsers());
        channelService.addUserToChannel(channel.getId(), userIds1);
        System.out.println(channel.getUsers());

        // USER - CHANNEL
        System.out.println(user.getChannels());


        // MESSAGE CRUD
        Message message = messageService.createMessage("배고파요", user.getId(), channel.getId());
//        System.out.println(messageService.findAll());

        messageService.updateMessage(message.getId(), "오늘 저녁 뭐 먹지");
//        System.out.println(messageService.findMessageById(message.getId()));

        messageService.deleteMessage(message.getId());
//        System.out.println(messageService.findAll());
    }
}