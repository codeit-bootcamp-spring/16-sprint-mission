package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;

import com.sprint.mission.discodeit.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Channel의 Message 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ApiSpecDto.MessageView>> findAll(
            @RequestParam("channelId") UUID channelId
    ) {
        List<ApiSpecDto.MessageView> messages =
                messageService.findAllByChannelId(channelId)
                        .stream()
                        .map(this::toMessageView)
                        .toList();

        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Message 생성")
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiSpecDto.MessageView> create(
            @RequestPart("messageCreateRequest")
            ApiSpecDto.MessageCreate request,

            @RequestPart(
                    value = "attachments",
                    required = false
            )
            List<MultipartFile> attachments
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "메시지 생성 정보가 없습니다."
            );
        }

        if (request.authorId() == null) {
            throw new IllegalArgumentException(
                    "authorId는 필수입니다."
            );
        }

        if (request.channelId() == null) {
            throw new IllegalArgumentException(
                    "channelId는 필수입니다."
            );
        }

        CreateMessageRequest createRequest =
                new CreateMessageRequest(
                        request.content(),
                        request.authorId(),
                        request.channelId()
                );

        MessageResponse message = messageService.create(
                createRequest,
                attachments
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toMessageView(message));
    }

    @Operation(summary = "Message 내용 수정")
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ApiSpecDto.MessageView> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody ApiSpecDto.MessageUpdate request
    ) {
        if (request == null
                || request.newContent() == null
                || request.newContent().isBlank()) {
            throw new IllegalArgumentException(
                    "newContent는 필수입니다."
            );
        }

        MessageResponse message = messageService.update(
                new UpdateMessageRequest(
                        messageId,
                        request.newContent()
                )
        );

        return ResponseEntity.ok(toMessageView(message));
    }

    @Operation(summary = "Message 삭제")
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

    private ApiSpecDto.MessageView toMessageView(
            MessageResponse message
    ) {
        return new ApiSpecDto.MessageView(
                message.id(),
                message.createdAt(),
                message.updatedAt(),
                message.content(),
                message.channelId(),
                message.userId(),
                message.attachmentIds()
        );
    }
}