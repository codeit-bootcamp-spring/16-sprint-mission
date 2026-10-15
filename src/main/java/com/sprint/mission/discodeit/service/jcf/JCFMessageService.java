package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User sender, Channel channel) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        if (sender == null || channel == null) {
            throw new IllegalArgumentException("메시지 작성자와 채널은 필수입니다.");
        }

        User validSender = this.userService.findById(sender.getId());
        Channel validChannel = this.channelService.findById(channel.getId());

        Message message = new Message(content, validSender, validChannel);
        this.data.add(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messageList = this.data.stream()
                .toList();

        return messageList;
    }

    @Override
    public Message findById(UUID uuid) {
        Message message = this.data.stream()
                .filter(m -> m.getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException());

        return message;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        Message message = this.findById(id);
        message.updateContent(content);

        return message;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message message = this.findById(uuid);
        this.data.remove(message);
    }

    @Override
    public void deleteMessageWithoutChannel() {
        this.data.removeIf(m ->
                !this.channelService.findAll().contains(m.getChannel()));
    }
}
