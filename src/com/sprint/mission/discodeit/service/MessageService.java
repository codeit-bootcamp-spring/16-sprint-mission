package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    MessageDto create(MessageCreateRequest request);

    MessageDto create(
            MessageCreateRequest request,
            List<MultipartFile> attachments
    );

    Optional<MessageDto> findById(UUID id);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID id);
}