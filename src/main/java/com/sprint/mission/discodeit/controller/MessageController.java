package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송(생성)-POST
    // PUBLIC 채널은 누구나, PRIVATE 채널은 참여자만 메시지를 생성할 수 있다.
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public MessageDto postMessage(
            @Valid @RequestPart("request") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments) {

        return messageService.createMessage(request, attachments);
    }

    // 특정 채널의 메시지 목록 조회(GET)
    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
    public List<MessageDto> getAllMessagesByChannelId(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // 메시지 수정(PUT)
    @RequestMapping(method = RequestMethod.PUT)
    public MessageDto putMessage(@Valid @RequestBody MessageUpdateRequest request) {
        return messageService.updateMessage(request);
    }

    // 메시지 삭제(DELETE)
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(
            @PathVariable UUID messageId,
            @RequestParam UUID requesterId) {
        messageService.deleteMessage(messageId, requesterId);
    }
}
