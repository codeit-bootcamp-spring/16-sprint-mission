package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
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

    @RequestMapping(method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Message create(@RequestPart("request") MessageCreateRequest request,
                          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        return messageService.create(request, attachments);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public Message update(@PathVariable("messageId") UUID messageId,
                          @RequestBody MessageUpdateRequest request) {
        MessageUpdateRequest updateRequest =
                new MessageUpdateRequest(messageId, request.comment());
        return messageService.update(updateRequest);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }
}