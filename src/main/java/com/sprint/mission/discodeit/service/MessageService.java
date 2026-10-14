package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content);
    List<Message> findAll();
    Message findById(UUID id);
    void updateContent(UUID id, String content);
    boolean deleteMessage(UUID id);
}
