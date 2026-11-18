package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserFindResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
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

@Tag(name = "User", description = "사용자")
public interface UserControllerDocs {
    @Operation(summary = "사용자 목록 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<UserFindResponse>> findAllUser();

    @Operation(summary = "사용자 등록")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "사용자가 성공적으로 등록됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 이름 또는 이메일의 사용자가 이미 존재함",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserResponse> createUser(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid
            @RequestPart("userCreateRequest") UserCreateRequest createRequest,
            @RequestPart(name = "profile", required = false) MultipartFile profileImage);

    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 이름, 이메일 또는 비밀번호를 수정함")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자가 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 이름 또는 이메일을 사용하는 사용자가 이미 존재함",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 사용자가 존재하지 않음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserResponse> updateUser(@Parameter(description = "사용자 ID") @NotNull @PathVariable UUID userId,
                                            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest updateRequest,
                                            @RequestPart(name = "profile", required = false) MultipartFile profileImage);

    @Operation(summary = "사용자 온라인 상태 갱신")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 온라인 상태가 성공적으로 갱신됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 사용자의 온라인 상태 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "갱신할 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(
            value = "/{userId}/userStatus",
            method = RequestMethod.PATCH)
    ResponseEntity<UserStatusResponse> updateUserStatus(@Parameter(description = "상태를 변경할 사용자 ID") @NotNull @PathVariable UUID userId,
                                                        @Valid @RequestBody UserStatusUpdateRequest updateRequest);

    @Operation(summary = "사용자 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "사용자 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteUser(@Parameter(description = "삭제할 사용자 ID") @NotNull @PathVariable UUID userId);
}
