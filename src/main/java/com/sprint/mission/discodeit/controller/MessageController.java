package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(
        name = "Message",
        description = "메시지 생성, 수정, 조회 및 삭제 API"
)
public class MessageController {
    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Message 생성",
            description = "채널 ID, 작성자 ID, 메시지 내용 및 선택적인 첨부파일을 이용하여 메시지를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440003",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "updatedAt": "2026-11-17T06:00:00Z",
                          "content": "안녕하세요!",
                          "channelId": "550e8400-e29b-41d4-a716-446655440001",
                          "authorId": "550e8400-e29b-41d4-a716-446655440002",
                          "attachmentIds": []
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "메시지 내용, 채널 ID 또는 작성자 ID의 유효성 검증 실패, 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Channel 또는 User가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "첨부파일 또는 전체 multipart 요청의 크기가 서버 제한을 초과했습니다."
            )
    })
    public ResponseEntity<MessageResponse> createMessage(
            @Parameter(
                    description = "메시지 내용, 채널 UUID, 작성자 UUID를 포함하는 필수 JSON 파트",
                    required = true
            )
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @Parameter(
                    description = "선택적으로 첨부하는 파일 목록"
            )
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageResponse messageResponse =
                this.messageService.create(request, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PatchMapping("/{messageId}")
    @Operation(
            summary = "Message 수정",
            description = "지정한 Message의 내용을 수정합니다. 현재 API에서는 첨부파일 수정을 지원하지 않습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440003",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "updatedAt": "2026-11-17T07:00:00Z",
                          "content": "수정된 메시지입니다.",
                          "channelId": "550e8400-e29b-41d4-a716-446655440001",
                          "authorId": "550e8400-e29b-41d4-a716-446655440002",
                          "attachmentIds": []
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 messageId UUID 또는 요청 JSON 형식",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 Message가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageResponse messageResponse = this.messageService.update(
                messageId, request, null);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/{messageId}")
    @Operation(
            summary = "Message 단건 조회",
            description = "Message ID를 기준으로 해당 메시지의 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440003",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "updatedAt": "2026-11-17T06:00:00Z",
                          "content": "안녕하세요!",
                          "channelId": "550e8400-e29b-41d4-a716-446655440001",
                          "authorId": "550e8400-e29b-41d4-a716-446655440002",
                          "attachmentIds": []
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "messageId의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Message가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<MessageResponse> findMessage(
            @PathVariable("messageId") UUID messageId
    ) {
        MessageResponse messageResponse = this.messageService.find(messageId);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping
    @Operation(
            summary = "Channel별 Message 목록 조회",
            description = "지정한 Channel에 속한 모든 메시지를 조회하고 JSON 배열로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = MessageResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "channelId가 누락되거나 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Channel이 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<List<MessageResponse>> findAllMessage(
            @RequestParam("channelId") UUID channelId
    ) {
        MessageListResponse messageResponseList =
                this.messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messageResponseList.messages());
    }

    @DeleteMapping("/{messageId}")
    @Operation(
            summary = "Message 삭제",
            description = "지정한 Message와 해당 메시지의 첨부파일을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제되었습니다. 응답 본문은 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "messageId의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 Message가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("messageId") UUID messageId
    ) {
        this.messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}
