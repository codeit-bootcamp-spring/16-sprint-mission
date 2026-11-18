package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.channel.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널")
public interface ChannelControllerDocs {
    @Operation(summary = "공개 채널 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "공개 채널이 성공적으로 생성됨"
            )
    })
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    ResponseEntity<ChannelResponse> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequest createRequest);

    @Operation(summary = "비공개 채널 생성")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "201",
                    description = "비공개 채널이 성공적으로 생성됨"
            )
    )
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    ResponseEntity<ChannelResponse> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest createRequest);

    @Operation(summary = "채널 가입")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "사용자가 채널에 성공적으로 가입함"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자의 해당 채널에 대한 메세지 읽음 상태가 이미 존재함",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{id}/users", method = RequestMethod.POST)
    ResponseEntity<Void> joinChannel(@Parameter(description = "가입하는 채널 ID") @NotNull @PathVariable UUID id,
                                     @Valid @RequestBody ChannelJoinRequest joinRequest);

    @Operation(summary = "채널 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "사용자가 채널에서 성공적으로 탈퇴함"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{id}/users", method = RequestMethod.DELETE)
    ResponseEntity<Void> leaveChannel(@Parameter(description = "탈퇴할 채널 ID") @NotNull @PathVariable UUID id,
                                      @Valid @RequestBody ChannelLeaveRequest leaveRequest);

    @Operation(summary = "공개 채널 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공개 채널이 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비공개 채널은 수정할 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 공개 채널을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    ResponseEntity<ChannelResponse> updatePublicChannel(@Parameter(description = "수정할 공개 채널 ID") @NotNull @PathVariable UUID channelId,
                                                        @RequestBody PublicChannelUpdateRequest updateRequest);

    @Operation(summary = "사용자가 참여 중인 채널 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자가 참여 중인 채널 전체 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<ChannelFindResponse>> findAllChannels(@Parameter(description = "사용자 ID") @NotNull @RequestParam UUID userId);

    @Operation(summary = "채널 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "채널이 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 채널을 찾을 수 없음"
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteChannel(@Parameter(description = "삭제할 채널 ID") @NotNull @PathVariable UUID channelId);
}
