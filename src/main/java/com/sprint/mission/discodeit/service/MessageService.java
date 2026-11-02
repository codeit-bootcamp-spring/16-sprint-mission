package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageRequestDto messageRequestDto);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto update(UUID messageId, MessageUpdateDto messageUpdateDto);
    void delete(UUID messageId);
}
