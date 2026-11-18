package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDto create(
            @RequestPart("messageCreateRequest") MessageCreateRequestDto request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) throws IOException {
        return messageService.createMessage(request, attachments);
    }

    @Override
    @GetMapping
    public List<MessageResponseDto> findAllChannelMessages(
            @RequestParam UUID channelId
    ) {
        return messageService.findAllMessagesByChannelId(channelId);
    }

    @Override
    @PatchMapping("/{messageId}")
    public MessageResponseDto update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequestDto request
    ) {
        return messageService.updateMessage(messageId, request);
    }

    @Override
    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID messageId
    ) {
        messageService.deleteMessage(messageId);
    }
}
