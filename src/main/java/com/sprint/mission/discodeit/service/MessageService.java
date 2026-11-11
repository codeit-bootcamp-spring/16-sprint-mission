package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Message.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageCreateResponse create(MessageCreateRequest createRequest,
                                 MessageAttachmentsCreateRequest attachmentsCreateRequest);

    MessageUpdateResponse update(MessageUpdateRequest updateRequest);

    MessageFindResponse findById(UUID id);

    List<MessageFindResponse> findAllByChannelId(UUID channelId);

    void delete(UUID id);
}
