package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.swagger.message.MessageCreateMultipartRequestDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "Channel을 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Channel을 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "채널을 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    List<MessageResponseDto> findAllChannelMessages(
            @RequestParam UUID channelId
    );

    @Operation(
            summary = "Message 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    implementation = MessageCreateMultipartRequestDto.class
                            ),
                            encoding = {
                                    @Encoding(
                                            name = "messageCreateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨")
    @ApiResponse(
            responseCode = "400",
            description = "비공개 채널에 참여하지 않은 사용자",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "채널 비참여자",
                            value = """
                                    {
                                      "status": 400,
                                      "message": "사용자가 해당 채널에 참여하고 있지 않습니다."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "메시지 내용 또는 첨부 파일이 필요함",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "메시지 내용 및 첨부 파일 누락",
                            value = """
                                    {
                                      "status": 400,
                                      "message": "메시지 내용 또는 첨부 파일은 필수입니다."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "채널 없음",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "채널을 찾을 수 없습니다. %s"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "사용자를 찾을 수 없습니다. %s"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "413",
            description = "파일 크기가 허용된 최대 크기를 초과함",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "파일 크기 초과",
                            value = """
                                    {
                                      "status": 413,
                                      "message": "파일 크기가 허용된 최대 크기를 초과했습니다."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "파일 처리 중 오류 발생",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(
                            name = "파일 처리 실패",
                            value = "파일 처리 중 오류가 발생했습니다."
                    )
            )
    )
    MessageResponseDto create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequestDto request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) throws IOException;

    @Operation(summary = "Message 삭제")
    @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨")
    @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Message를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "메시지를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    void delete(@PathVariable UUID messageId);

    @Operation(summary = "Message 내용 수정")
    @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨")
    @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Message를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "메시지를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    MessageResponseDto update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequestDto request
    );
}
