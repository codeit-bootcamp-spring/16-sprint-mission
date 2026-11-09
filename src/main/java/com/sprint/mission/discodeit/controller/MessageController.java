package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Message create(@RequestBody MessageCreateRequest request) {
        return messageService.create(request, List.of());
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