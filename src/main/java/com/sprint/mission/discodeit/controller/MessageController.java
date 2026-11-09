package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;

import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> create(
            @RequestBody CreateMessageRequest request
    ) {
        MessageResponse response =
                messageService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(
            @RequestParam("channelId") UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.GET
    )
    public MessageResponse findById(
            @PathVariable("messageId") UUID messageId
    ) {
        return messageService.findById(messageId);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH
    )
    public MessageResponse update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody UpdateMessageRequest request
    ) {
        if (request.id() != null
                && !messageId.equals(request.id())) {
            throw new IllegalArgumentException(
                    "URL의 메시지 ID와 요청 본문의 ID가 다릅니다."
            );
        }

        UpdateMessageRequest updateRequest =
                new UpdateMessageRequest(
                        messageId,
                        request.content()
                );

        return messageService.update(updateRequest);
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
}