package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse create(
            CreateMessageRequest request,
            List<MultipartFile> attachments
    );

    MessageResponse findById(UUID id);

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse update(UpdateMessageRequest request);

    void delete(UUID id);
}