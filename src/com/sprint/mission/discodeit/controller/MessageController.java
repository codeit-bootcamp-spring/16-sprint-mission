package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송 (첨부파일 선택 가능)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> create(
            @RequestPart("request") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageDto messageDto = messageService.create(request, attachments);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageDto);
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(
            value = "/channels/{channelId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @PathVariable UUID channelId
    ) {
        return ResponseEntity.ok(
                messageService.findAllByChannelId(channelId)
        );
    }

    // 메시지 수정
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(
                messageId,
                request.content()
        );

        return ResponseEntity.ok(
                messageService.update(updateRequest)
        );
    }

    // 메시지 삭제
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}