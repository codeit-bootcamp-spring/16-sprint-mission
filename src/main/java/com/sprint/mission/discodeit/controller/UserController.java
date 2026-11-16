package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag; // API 그룹 이름과 설명
import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.Parameter; // PathVariable 등의 파라미터 설명
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.responses.ApiResponses; // API의 응답 목록 정의

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/users"})
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    /* 사용자 등록(POST) */
    @Operation(summary = "User 등록")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "The email or username is already in use."
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    encoding = {
                            @Encoding(
                                    name = "userCreateRequest",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @Parameter(
                    description = "User 프로필 이미지",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserDto user = userService.createUser(request, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /* 전체 사용자 조회(GET) */
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(
            responseCode = "200",
            description = "User 목록 조회 성공"
    )
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.readAllUser());
    }

    /* 사용자 정보 수정(PATCH) */
    @Operation(summary = "User 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "User with id {userId} not found"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "The email or username is already in use."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    encoding = {
                            @Encoding(
                                    name = "userUpdateRequest",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto patchUser(
            @Parameter(
                    description = "수정할 User ID",
                    required = true
            )
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        return userService.updateUser(userId, request, profile);
    }

    /* 사용자 삭제(DELETE) */
    @Operation(summary = "User 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제돰"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "User with id {id} not found"
                            )
                    )

            )
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "삭제할 User ID",
                    required = true
            )
            @PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}