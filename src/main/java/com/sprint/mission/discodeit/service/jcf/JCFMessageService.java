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

    @Override
    public boolean sendMessage(Message message) {
        userService.getUserById(message.getSenderId());
        channelService.getChannelById(message.getChannelId());

        messageData.put(message.getId(), message);
        return true;
    }

    @Override
    public boolean updateMessage(UUID messageId, String content) {
        Message message = messageData.get(messageId);
        message.updateMessage(content);
        return true;
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        messageData.remove(messageId);
        return true;
    }

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
}
