package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateRequest request, List<BinaryContentRequest> attachmentRequest);
    MessageDto findById(UUID id);
    List<MessageDto> findAll();
    List<MessageDto> findAllByChannelId(UUID channelId);
    MessageDto update(UUID id, MessageUpdateRequest request);
    void delete(UUID id, UUID requesterId);
}
