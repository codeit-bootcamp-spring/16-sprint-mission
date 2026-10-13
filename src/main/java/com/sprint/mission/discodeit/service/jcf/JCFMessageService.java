package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
        User user = userService.getUserById(message.getSenderId());
        Channel channel = channelService.getChannelById(message.getChannelId());

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

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

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId){
        return messageData.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }
}
