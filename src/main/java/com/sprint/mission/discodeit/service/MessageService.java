package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(List<BinaryContentRequest> binaryContents, MessageCreateRequest request);
    MessageResponse find(UUID messageId);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(UUID messageId, UpdateMessageRequest request);
    void delete(UUID messageId);
}
