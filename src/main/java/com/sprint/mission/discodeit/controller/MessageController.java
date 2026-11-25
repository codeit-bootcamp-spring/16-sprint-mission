package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.CustomErrorResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록한 메시지 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "2a3de7b2-39d4-4df3-8545-b253b6f6c413",
                                      "createdAt": "2026-11-16T05:30:00Z",
                                      "updatedAt": "2026-11-16T05:30:00Z",
                                      "content": "안녕하세요",
                                      "channelId": "6dd80dd7-c1b2-4690-ac2a-9f58aefda3a2",
                                      "authorId": "2141551d-6dd9-4deb-881a-e65be0472bcd",
                                      "attachmentIds": []
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "작성자나 채널을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"404\",\"message\":\"채널을 찾을 수 없음\"}")))
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse create(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Message message = messageService.create(request, attachments);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @Operation(summary = "메시지 목록 조회", description = "채널 ID로 해당 채널에 등록된 메시지 목록 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널의 메시지 목록 반환"),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId).stream()
                .map(message -> new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                        message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds()))
                .toList();
    }

    @Operation(summary = "메시지 수정", description = "메시지 ID로 메시지를 찾아 내용을 수정하고 수정된 메시지 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정한 메시지 반환"),
            @ApiResponse(responseCode = "404", description = "메시지나 작성자 또는 채널을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public MessageResponse update(@PathVariable("messageId") UUID messageId,
                                  @RequestBody MessageUpdateRequest request) {
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(messageId, request.newContent());
        Message message = messageService.update(updateRequest);
        return new MessageResponse(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getComment(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @Operation(summary = "메시지 삭제", description = "메시지 ID로 메시지를 찾아 첨부파일과 함께 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "메시지나 채널을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }
}
