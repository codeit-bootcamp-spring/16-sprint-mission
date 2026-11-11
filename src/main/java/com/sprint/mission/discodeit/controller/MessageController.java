package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @Valid @RequestPart("request") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageResponse messageResponse = this.messageService.create(request, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PatchMapping("/{message-id}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable("message-id") UUID messageId,
            @RequestPart("request")MessageUpdateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageResponse messageResponse = this.messageService.update(messageId, request, attachments);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/{message-id}")
    public ResponseEntity<MessageResponse> findMessage(@PathVariable("message-id") UUID messageId) {
        MessageResponse messageResponse = this.messageService.find(messageId);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/{channel-id}/channel")
    public ResponseEntity<MessageListResponse> findAllMessage(@PathVariable("channel-id") UUID channelId) {
        List<MessageResponse> messageResponseList = this.messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(new MessageListResponse(messageResponseList));
    }

    @DeleteMapping("/{message-id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        this.messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}
