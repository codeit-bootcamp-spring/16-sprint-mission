package com.sprint.mission.discodeit.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class DiscordService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public DiscordService(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public User createUser(String userName, String email, String password) {
        return userService.create(userName, email, password);
    }

    public Channel createChannel(ChannelType channelType, String title, String description) {
        return channelService.create(channelType, title, description);
    }

    public User findUserById(UUID userId) {
        return userService.findById(userId);
    }

    public User findUserByName(String userName) {
        return userService.findByName(userName);
    }

    public List<User> findAllUsers() {
        return userService.findAll();
    }

    public Channel findChannelById(UUID channelId) {
        return channelService.findById(channelId);
    }

    public List<Channel> findAllChannels() {
        return channelService.findAll();
    }

    public void createMessage(String msg, Channel channel, User author) {
        Message message = messageService.create(msg, channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public Message findMsgById(UUID messageId) {
        return messageService.findById(messageId);
    }

    public List<Message> findMsgByUsername(String userName) {
        User user = findUserByName(userName);
        List<Message> allMessages = findAllMessages();

        List<Message> userMessageList = allMessages.stream()
                .filter(message -> message.getUserId().equals(user.getId()))
                .toList();

        return userMessageList;
    }

    public List<Message> findAllMessages() {
        return messageService.findAll();
    }
}
