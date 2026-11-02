package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageDto createMessage(MessageCreateDto dto);

    MessageDto findMessageById(UUID id);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto updateMessage(MessageUpdateDto dto);

    void deleteMessage(UUID id);

    void deleteByUserId(UUID userId);

    void deleteByChannelId(UUID channelId);
}