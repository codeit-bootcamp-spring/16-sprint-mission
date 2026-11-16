package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageListResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request, List<MultipartFile> attachments);

    MessageResponse find(UUID messageId);

    MessageListResponse findAllByChannelId(UUID channelId);

    MessageResponse update(UUID messageId, MessageUpdateRequest request, List<MultipartFile> attachments);

    void delete(UUID messageId);
}
