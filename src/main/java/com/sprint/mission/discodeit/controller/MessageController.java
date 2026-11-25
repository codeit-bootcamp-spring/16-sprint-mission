package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "메시지 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MessageCreateMultipartBody.class),
                            encoding = @Encoding(
                                    name = "messageCreateRequest",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            )
    )
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") String requestJson,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessagePart request;
        try {
            request = objectMapper.readValue(requestJson, MessagePart.class);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException(
                    "messageCreateRequest의 JSON 형식이 올바르지 않습니다.", exception
            );
        }
        if (request == null) {
            throw new IllegalArgumentException("messageCreateRequest는 JSON 객체여야 합니다.");
        }

        List<BinaryContentCreateRequest> files = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                try {
                    files.add(new BinaryContentCreateRequest(
                            attachment.getOriginalFilename(),
                            attachment.getContentType(),
                            attachment.getBytes()
                    ));
                } catch (IOException exception) {
                    throw new IllegalArgumentException("첨부 파일 처리에 실패했습니다.", exception);
                }
            }
        }

        MessageCreateRequest serviceRequest = new MessageCreateRequest(
                request.content(), request.channelId(), request.authorId(), files
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.create(serviceRequest));
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateBody request
    ) {
        MessageUpdateRequest serviceRequest = new MessageUpdateRequest(
                messageId,
                request.newContent()
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

    public record MessageUpdateBody(String newContent) {
    }

    public record MessageCreateMultipartBody(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) MessagePart messageCreateRequest,
            List<MultipartFile> attachments
    ) {
    }

    public record MessagePart(String content, UUID channelId, UUID authorId) {
    }

}