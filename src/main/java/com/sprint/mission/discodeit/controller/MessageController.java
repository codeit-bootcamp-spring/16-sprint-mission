package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Message", description = "메시지 등록, 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "메시지 등록",
            description = "messageCreateRequest에 메시지 정보를 담고, attachments에 파일을 첨부할 수 있습니다."
    )
    @ApiResponse(responseCode = "201", description = "메시지 등록 성공")
    @ApiResponse(
            responseCode = "400",
            description = "필수 메시지 파트가 누락되었거나 JSON을 읽을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "작성자 또는 채널을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 또는 멀티파트 항목의 Content-Type을 지원하지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList())
                .orElse(new ArrayList<>());
        Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }


    @Operation(summary = "메시지 본문 수정")
    @ApiResponse(responseCode = "200", description = "메시지 수정 성공")
    @ApiResponse(
            responseCode = "400",
            description = "메시지 ID의 UUID 형식이 잘못되었거나 요청 JSON을 읽을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "수정할 메시지를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PatchMapping(value = "/{messageId}")
    public ResponseEntity<Message> update(@PathVariable("messageId") UUID messageId,
                                          @RequestBody MessageUpdateRequest request) {
        Message updatedMessage = messageService.update(messageId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }


    @Operation(summary = "메시지 삭제")
    @ApiResponse(responseCode = "204", description = "메시지 삭제 성공", content = @Content)
    @ApiResponse(
            responseCode = "400",
            description = "메시지 ID의 UUID 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "삭제할 메시지를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @DeleteMapping(value = "/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "채널별 메시지 목록 조회")
    @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "channelId가 누락되었거나 UUID 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}
