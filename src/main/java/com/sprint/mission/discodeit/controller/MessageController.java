package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "메시지 생성", description = "채널에 메시지를 보냅니다. 첨부파일은 선택이며 여러 개 보낼 수 있습니다.")
    // JSON 파트의 Content-Type을 명시해야 Swagger UI에서 보낸 요청이 415로 거절되지 않는다
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "404", description = "해당 채널 또는 작성자가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendMessage(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                                       @RequestPart(value = "attachments", required = false) List<MultipartFile> files) throws IOException {
        List<BinaryContentRequest> attachments = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                attachments.add(new BinaryContentRequest(file.getOriginalFilename(), file.getSize(),
                        file.getContentType(), file.getBytes()));
            }
        }
        MessageResponse response = messageService.create(attachments, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "메시지 수정", description = "메시지 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 메시지가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(@RequestBody UpdateMessageRequest request,
                                                         @PathVariable("messageId") UUID messageId) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content)
    @ApiResponse(responseCode = "404", description = "해당 메시지가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{messageId}")
    public ResponseEntity deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "채널의 메시지 목록 조회", description = "해당 채널의 모든 메시지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 채널이 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(@RequestParam UUID channelId) {
        List<MessageResponse> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }


}
