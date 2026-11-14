package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /* 메시지 전송(생성)-POST */
    // PUBLIC 채널은 누구나, PRIVATE 채널은 참여자만 메시지를 생성할 수 있다.
    // 생성 성공 시 201 Created 반환
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> postMessage(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments) {

        MessageDto message = messageService.createMessage(request, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /* 특정 채널의 메시지 목록 조회(GET) */
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageDto> getAllMessagesByChannelId(
            @RequestParam UUID channelId) {

        return messageService.findAllByChannelId(channelId);
    }

    /* 메시지 수정(PATCH) */
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public MessageDto patchMessage(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {

        return messageService.updateMessage(messageId, request);
    }

    /* 메시지 삭제(DELETE) */
    // 삭제 성공 시 204 No Content 반환
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId) {

        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
