package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    boolean sendMessage(Message message);

    boolean updateMessage(UUID messageId, String content);

    boolean deleteMessage(UUID messageId);

    List<Message> getMessagesByUserId(UUID userId);

    List<Message> getMessagesByChannelId(UUID channelId);
}
