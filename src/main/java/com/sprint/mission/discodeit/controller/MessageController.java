package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.annotation.swagger.message.MessageCreateDocs;
import com.sprint.mission.discodeit.annotation.swagger.message.MessageDeleteDocs;
import com.sprint.mission.discodeit.annotation.swagger.message.MessageReadDocs;
import com.sprint.mission.discodeit.annotation.swagger.message.MessageUpdateDocs;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag; // API 그룹 이름과 설명

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    @MessageCreateDocs
    @RequestMapping(
            method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> postMessage(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @Parameter(description = "Message 첨부 파일들")
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments) {

        MessageDto message = messageService.createMessage(request, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @MessageReadDocs
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageDto> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID")
            @RequestParam UUID channelId) {

        return messageService.findAllByChannelId(channelId);
    }

    @MessageUpdateDocs
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public MessageDto patchMessage(
            @Parameter(description = "수정할 Message ID")
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {

        return messageService.updateMessage(messageId, request);
    }

    @MessageDeleteDocs
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "삭제할 Message ID")
            @PathVariable UUID messageId) {

        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
