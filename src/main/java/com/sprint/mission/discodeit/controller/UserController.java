package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.repository.UserStatusRepository;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserStatusRepository userStatusRepository;

    @Operation(summary = "전체 User 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {

        List<UserDto> users = userService.findAll()
                .stream()
                .map(this::toUserDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "User 등록")
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiSpecDto.UserView> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = @Encoding(
                                    name = "userCreateRequest",
                                    contentType = "application/json"
                            )
                    )
            )
            @RequestPart("userCreateRequest")
            ApiSpecDto.UserCreate request,

            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "회원가입 정보가 없습니다."
            );
        }

        CreateUserRequest createRequest =
                new CreateUserRequest(
                        request.username(),
                        request.email(),
                        request.password()
                );

        UserResponse user = userService.create(
                createRequest,
                profile
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toUserView(user));
    }

    @Operation(hidden = true)
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<UserDto> findById(
            @PathVariable("userId") UUID userId
    ) {
        return ResponseEntity.ok(
                toUserDto(userService.findById(userId))
        );
    }

    @Operation(summary = "User 정보 수정")
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiSpecDto.UserView> update(
            @PathVariable("userId") UUID userId,

            @RequestPart("userUpdateRequest")
            ApiSpecDto.UserUpdate request,

            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "회원정보 수정 정보가 없습니다."
            );
        }

        UpdateUserRequest updateRequest =
                new UpdateUserRequest(
                        userId,
                        request.newUsername(),
                        request.newEmail(),
                        request.newPassword()
                );

        UserResponse updatedUser = userService.update(
                updateRequest,
                profile
        );

        return ResponseEntity.ok(toUserView(updatedUser));
    }

    @Operation(summary = "User 삭제")
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("userId") UUID userId
    ) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "User 온라인 상태 업데이트")
    @RequestMapping(
            value = "/{userId}/userStatus",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ApiSpecDto.UserStatusView> updateUserStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody ApiSpecDto.UserStatusUpdate request
    ) {
        if (request == null
                || request.newLastActiveAt() == null) {
            throw new IllegalArgumentException(
                    "newLastActiveAt이 필요합니다."
            );
        }

        userStatusService.updateByUserId(
                new UpdateUserStatusByUserIdRequest(
                        userId,
                        request.newLastActiveAt()
                )
        );

        UserStatus status =
                userStatusRepository.findByUserId(userId);

        return ResponseEntity.ok(
                new ApiSpecDto.UserStatusView(
                        status.getId(),
                        status.getCreatedAt(),
                        status.getUpdatedAt(),
                        status.getUserId(),
                        status.getLastSeenAt(),
                        status.isOnline()
                )
        );
    }

    private UserDto toUserDto(UserResponse user) {
        return new UserDto(
                user.id(),
                user.createdAt(),
                user.updatedAt(),
                user.username(),
                user.email(),
                user.profileId(),
                user.online()
        );
    }

    private ApiSpecDto.UserView toUserView(
            UserResponse user
    ) {
        return new ApiSpecDto.UserView(
                user.id(),
                user.createdAt(),
                user.updatedAt(),
                user.username(),
                user.email(),
                user.profileId()
        );
    }
}