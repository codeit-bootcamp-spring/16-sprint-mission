package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest createRequest,
                           List<MultipartFile> attachments);

    MessageResponse update(UUID id, MessageUpdateRequest updateRequest);

    MessageResponse findById(UUID id);

    List<MessageResponse> findAllByChannelId(UUID channelId);

    void delete(UUID id);
}
