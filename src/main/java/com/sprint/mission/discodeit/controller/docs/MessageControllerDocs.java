package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메세지")
public interface MessageControllerDocs {
    @Operation(summary = "채널의 메세지 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메세지 전체 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<MessageResponse>> findAllMessagesByChannelId(@Parameter(description = "채널 ID") @NotNull @RequestParam UUID channelId);

    @Operation(summary = "메세지 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "메세지 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MessageResponse> createMessage(@RequestPart(name = "attachments", required = false) List<MultipartFile> attachments,
                                                  @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                  @Valid @RequestPart("messageCreateRequest") MessageCreateRequest createRequest);

    @Operation(summary = "메세지 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메세지 내용 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 메세지를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    ResponseEntity<MessageResponse> updateMessage(@Parameter(description = "메세지 ID") @NotNull @PathVariable UUID messageId,
                                                  @RequestBody MessageUpdateRequest updateRequest);

    @Operation(summary = "메세지 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "메세지 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "메세지를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteMessage(@Parameter(description = "메세지 ID") @NotNull @PathVariable UUID messageId);
}
