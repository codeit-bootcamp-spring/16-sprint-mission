package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
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

@Tag(name = "ReadStatus", description = "메세지 읽음 상태")
public interface ReadStatusControllerDocs {
    @Operation(summary = "사용자의 메세지 읽음 상태 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자의 메세지 읽음 상태 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<ReadStatusResponse>> findAllReadStatusByUserId(@Parameter(description = "사용자 ID") @NotNull @RequestParam UUID userId);

    @Operation(summary = "메세지 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "메세지 읽음 상태 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "메세지 읽음 상태가 이미 존재함",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널이 존재하지 않음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<ReadStatusResponse> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest createRequest);

    @Operation(summary = "메세지 읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메세지 읽음 상태 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 메세지 읽음 상태를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    ResponseEntity<ReadStatusResponse> updateReadStatus(@Parameter(description = "메세지 읽음 상태 ID") @NotNull @PathVariable UUID readStatusId,
                                                        @Valid @RequestBody ReadStatusUpdateRequest updateRequest);
}
