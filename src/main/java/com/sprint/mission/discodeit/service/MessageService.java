package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Message createMessage (String content, UUID userId, UUID channelId);

    Optional<Message> findMessageById(UUID id);

    List<Message> findAll();

    Message updateMessage(UUID id, String content);

    void deleteMessage (UUID id);

    void deleteByUserId(UUID userId);

    void deleteByChannelId(UUID channelId);
}