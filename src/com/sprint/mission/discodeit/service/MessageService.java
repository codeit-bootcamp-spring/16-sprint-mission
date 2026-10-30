package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Message create(MessageCreateRequest request);

    Optional<Message> findById(UUID id);

    List<Message> findAllByChannelId(UUID channelId);

    Message update(MessageUpdateRequest request);

    void delete(UUID id);
}