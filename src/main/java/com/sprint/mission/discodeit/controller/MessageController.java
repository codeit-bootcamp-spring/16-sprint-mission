package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "메시지", description = "메시지 관리 API")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "메시지 등록", description = "채널에 메시지를 등록하고 첨부파일이 있으면 함께 저장한 뒤 등록된 메시지 정보 반환")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse create(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Message message = messageService.create(request, attachments);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @Operation(summary = "메시지 목록 조회", description = "채널 ID로 해당 채널에 등록된 메시지 목록 반환")
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId).stream()
                .map(message -> new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                        message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds()))
                .toList();
    }

    @Operation(summary = "메시지 수정", description = "메시지 ID로 메시지를 찾아 내용을 수정하고 수정된 메시지 정보 반환")
    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public MessageResponse update(@PathVariable("messageId") UUID messageId,
                                  @RequestBody MessageUpdateRequest request) {
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(messageId, request.newContent());
        Message message = messageService.update(updateRequest);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @Operation(summary = "메시지 삭제", description = "메시지 ID로 메시지를 찾아 첨부파일과 함께 삭제")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }
}