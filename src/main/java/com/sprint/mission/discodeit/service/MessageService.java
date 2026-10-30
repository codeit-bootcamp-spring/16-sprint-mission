package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDto createMessage(MessageCreateRequestDto dto);

    MessageResponseDto createMessage(
            MessageCreateRequestDto dto,
            List<BinaryContentCreateRequestDto> attachments);

    MessageResponseDto findMessage(UUID messageId);

    List<MessageResponseDto> findAllMessagesByChannelId(UUID channelId);

    MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequestDto dto);

    void deleteMessage(UUID messageId);
}
