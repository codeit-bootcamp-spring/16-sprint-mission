package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends SaveObject<Message> {
    // Message saveMessage(Message message);
    List<Message> getMessages();
    Message getMessage(UUID id);
    void removeMessage(UUID id);
}