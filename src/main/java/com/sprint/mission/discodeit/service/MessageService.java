package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(
            String content,
            UUID userId,
            UUID channelId
    );

    Message findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    void delete(UUID id);

    default Message create(CreateMessageRequest request) {
        return create(
                request.content(),
                request.userId(),
                request.channelId()
        );
    }

    default List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(message ->
                        message.getChannelId().equals(channelId))
                .toList();
    }

    default Message update(UpdateMessageRequest request) {
        return update(
                request.id(),
                request.content()
        );
    }
}