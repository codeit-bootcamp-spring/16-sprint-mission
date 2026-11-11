package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(CreateMessageRequest request);
    MessageResponse find(UUID messageId);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(UUID messageId, UpdateMessageRequest request);
    void delete(UUID messageId);
}
