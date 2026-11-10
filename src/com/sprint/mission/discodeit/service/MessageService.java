package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    MessageDto create(MessageCreateRequest request);

    Optional<MessageDto> findById(UUID id);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto update(MessageUpdateRequest request);

    void delete(UUID id);
}