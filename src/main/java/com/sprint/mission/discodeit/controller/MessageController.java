package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> create(
            @RequestBody MessageCreateRequest request
    ) {
        Message message = messageService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageUpdateRequest serviceRequest = new MessageUpdateRequest(
                messageId,
                request.content()
        );

        messageService.update(serviceRequest);

        Message message = messageService.findById(messageId);

        return ResponseEntity.ok(message);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("messageId") UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messages);
    }
}