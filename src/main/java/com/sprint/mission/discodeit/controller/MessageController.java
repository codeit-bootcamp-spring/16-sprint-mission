package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponseDto create(
            @RequestPart("request") MessageCreateRequestDto request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) throws IOException {
        return messageService.createMessage(request, attachments);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponseDto> findAllChannelMessages(
            @RequestHeader("CHANNEL-UUID") UUID channelId
    ) {
        return messageService.findAllMessagesByChannelId(channelId);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public MessageResponseDto update(
            @RequestHeader("MESSAGE-UUID") UUID messageId,
            @RequestBody MessageUpdateRequestDto request
    ) {
        return messageService.updateMessage(messageId,  request);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(
            @RequestHeader("MESSAGE-UUID") UUID messageId
    ) {
        messageService.deleteMessage(messageId);
    }
}
