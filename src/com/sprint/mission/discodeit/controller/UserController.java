package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "User", description = "사용자 및 사용자 상태 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @Operation(
            summary = "사용자 등록",
            description = "새로운 사용자를 등록합니다. "
                    + "사용자 정보는 JSON으로 전달하고 프로필 이미지는 선택적으로 첨부할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "사용자 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 잘못되었거나 이미 사용 중인 이메일 또는 사용자 이름",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 정보와 선택적 프로필 이미지. "
                            + "userCreateRequest는 JSON, profile은 이미지 파일입니다.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(
                                            name = "userCreateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "profile",
                                            contentType = "image/*"
                                    )
                            }
                    )
            )
            @RequestPart("userCreateRequest") UserCreateRequest request,

            @Parameter(
                    description = "사용자 프로필 이미지 파일 (선택 사항)"
            )
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) {
        UserDto userDto = userService.create(request, profile);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDto);
    }

    // 모든 사용자 조회
    @Operation(
            summary = "모든 사용자 조회",
            description = "등록된 모든 사용자 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 사용자 정보 수정
    @Operation(
            summary = "사용자 정보 수정",
            description = "사용자 이름, 이메일, 비밀번호를 수정합니다. "
                    + "프로필 이미지는 선택적으로 변경할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 잘못되었거나 이미 사용 중인 이메일 또는 사용자 이름",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> update(
            @Parameter(
                    description = "수정할 사용자 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 사용자 정보와 선택적 프로필 이미지. "
                            + "userUpdateRequest는 JSON, profile은 이미지 파일입니다.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(
                                            name = "userUpdateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "profile",
                                            contentType = "image/*"
                                    )
                            }
                    )
            )
            @RequestPart("userUpdateRequest") UserUpdateRequest request,

            @Parameter(
                    description = "변경할 프로필 이미지 파일 (선택 사항)"
            )
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) {
        return ResponseEntity.ok(
                userService.update(userId, request, profile)
        );
    }

    // 사용자 삭제
    @Operation(
            summary = "사용자 삭제",
            description = "지정한 사용자와 관련된 정보를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "사용자 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "삭제할 사용자 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID userId
    ) {
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    // 사용자 상태 수정
    @Operation(
            summary = "사용자 상태 수정",
            description = "지정한 사용자의 마지막 활동 시간을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 상태 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserStatusDto.class)
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
                    description = "존재하지 않는 사용자",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{userId}/userStatus",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatusDto> updateStatus(
            @Parameter(
                    description = "상태를 수정할 사용자 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 상태 수정 정보",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody
            UserStatusUpdateRequest request
    ) {
        UserStatusDto statusDto =
                userStatusService.updateByUserId(userId, request);

        return ResponseEntity.ok(statusDto);
    }
}