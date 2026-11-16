package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse create(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Message message = messageService.create(request, attachments);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId).stream()
                .map(message -> new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                        message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds()))
                .toList();
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public MessageResponse update(@PathVariable("messageId") UUID messageId,
                                  @RequestBody MessageUpdateRequest request) {
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(messageId, request.newContent());
        Message message = messageService.update(updateRequest);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }
}