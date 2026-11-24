package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;

    @Override
    public ResponseEntity<Message> createMessage(MessageCreateRequest request, List<MultipartFile> attachments) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(request, attachments));
    }

    @Override
    public ResponseEntity<List<Message>> findAllByChannelId(UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @Override
    public ResponseEntity<Message> updateMessage(UUID messageId, MessageUpdateRequest request) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @Override
    public ResponseEntity<Void> deleteMessage(UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}