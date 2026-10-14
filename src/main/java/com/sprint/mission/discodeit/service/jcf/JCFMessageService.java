package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageData =  new HashMap<>();

    private UserService userService;
    private ChannelService channelService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        Message message = messageData.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        return message;
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return messageData.values().stream()
                .filter(m -> m.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId){
        return messageData.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void sendMessage(Message message) {
        userService.getUserById(message.getSenderId());
        channelService.getChannelById(message.getChannelId());

        messageData.put(message.getId(), message);
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Message message = getMessageById(messageId);
        message.updateMessage(content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        getMessageById(messageId);
        messageData.remove(messageId);
    }
}
