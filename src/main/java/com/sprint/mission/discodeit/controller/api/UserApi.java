package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.swagger.user.UserCreateMultipartRequestDto;
import com.sprint.mission.discodeit.dto.swagger.user.UserUpdateMultipartRequestDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
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

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(
            summary = "User 등록",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    implementation = UserCreateMultipartRequestDto.class
                            ),
                            encoding = {
                                    @Encoding(
                                            name = "userCreateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨")
    @ApiResponse(
            responseCode = "400",
            description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
            content = @Content(
                    mediaType = MediaType.ALL_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "이메일 중복",
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "이미 사용 중인 이메일입니다."
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "사용자 이름 중복",
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "이미 사용 중인 사용자 이름입니다."
                                            }
                                            """
                            )
                    }
            )
    )
    UserResponseDto create(
            @RequestPart("userCreateRequest") UserCreateRequestDto request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException;

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    ResponseEntity<List<UserDto>> findAll();

    @Operation(
            summary = "User 정보 수정",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    implementation = UserUpdateMultipartRequestDto.class
                            ),
                            encoding = {
                                    @Encoding(
                                            name = "userUpdateRequest",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨")
    @ApiResponse(
            responseCode = "400",
            description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
            content = @Content(
                    mediaType = MediaType.ALL_VALUE,
                    examples = {
                            @ExampleObject(
                                    name = "이메일 중복",
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "이미 사용 중인 이메일입니다."
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "사용자 이름 중복",
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "이미 사용 중인 사용자 이름입니다."
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "User를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "사용자를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    UserResponseDto update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequestDto request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException;

    @Operation(summary = "User 삭제")
    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
    @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "User를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "사용자를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    void delete(@PathVariable UUID userId);

    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨")
    @ApiResponse(
            responseCode = "404",
            description = "해당 User의 UserStatus를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "해당 User의 UserStatus를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "사용자 상태를 찾을 수 없습니다."
                                    }
                                    """
                    )
            )
    )
    UserStatusResponseDto online(@PathVariable UUID userId);
}