package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 생성, 조회, 수정 및 삭제 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @Operation(
            summary = "메시지 생성",
            description = "새로운 메시지를 생성합니다. "
                    + "메시지 내용과 작성자 및 채널 정보는 JSON으로 전달하고 "
                    + "첨부파일은 선택적으로 여러 개 전달할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "메시지 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = MessageDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 잘못됨",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "채널 또는 작성자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "메시지 생성 정보와 선택적 첨부파일",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(
                                            name = "messageCreateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "attachments",
                                            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                                    )
                            }
                    )
            )
            @RequestPart("messageCreateRequest")
            MessageCreateRequest request,

            @Parameter(
                    description = "메시지에 첨부할 파일 목록 (선택 사항)"
            )
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageDto messageDto = messageService.create(request, attachments);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageDto);
    }

    // 특정 채널의 메시지 목록 조회
    @Operation(
            summary = "채널별 메시지 목록 조회",
            description = "지정한 채널에 등록된 메시지 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "채널 ID 형식이 올바르지 않음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @Parameter(
                    description = "메시지를 조회할 채널 ID (UUID)",
                    required = true,
                    example = "4fd2e346-ad09-41cd-a879-336ed7915f46"
            )
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(
                messageService.findAllByChannelId(channelId)
        );
    }

    // 메시지 수정
    @Operation(
            summary = "메시지 수정",
            description = "지정한 메시지의 내용을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = MessageDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 잘못됨",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 메시지",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MessageDto> update(
            @Parameter(
                    description = "수정할 메시지 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID messageId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 메시지 내용",
                    required = true
            )
            @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(
                messageService.update(messageId, request)
        );
    }

    // 메시지 삭제
    @Operation(
            summary = "메시지 삭제",
            description = "지정한 메시지를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "메시지 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 메시지",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "삭제할 메시지 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}