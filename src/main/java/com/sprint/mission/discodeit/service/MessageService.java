package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(User sentFrom, String content, Channel channel);

    Message updateContent(UUID id, String content);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    void delete(UUID id);

    void validateNotDeleted(Message message);
}
