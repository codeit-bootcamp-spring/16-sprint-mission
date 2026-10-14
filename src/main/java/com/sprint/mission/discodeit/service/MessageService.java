package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message getMessageById(UUID messageId);

    List<Message> getMessagesByUserId(UUID userId);

    List<Message> getMessagesByChannelId(UUID channelId);

    void sendMessage(Message message);

    void updateMessage(UUID messageId, String content);

    void deleteMessage(UUID messageId);
}
