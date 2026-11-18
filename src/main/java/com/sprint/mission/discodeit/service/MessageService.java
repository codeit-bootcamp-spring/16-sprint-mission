package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageCreateResponse create(MessageCreateRequest createRequest,
                                 List<MultipartFile> attachments);

    MessageUpdateResponse update(UUID id, MessageUpdateRequest updateRequest);

    MessageFindResponse findById(UUID id);

    List<MessageFindResponse> findAllByChannelId(UUID channelId);

    void delete(UUID id);
}
