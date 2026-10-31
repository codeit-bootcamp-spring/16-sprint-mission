package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageDto create(MessageCreateRequest request);

    MessageDto find(UUID messageId);

    List<MessageDto> findAll();

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto update(MessageUpdateRequest request);

    void delete(UUID messageId);
}
