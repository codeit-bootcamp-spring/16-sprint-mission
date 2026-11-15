package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {
        UserDto userDto = userService.create(request, profile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDto);
    }

    @PatchMapping(value = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile) throws IOException {
        UserDto userDto = this.userService.update(
                userId, request, profile
        );

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("userId") UUID userId
    ) {
        this.userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        UserListResponse userListResponse = this.userService.findAll();


        return ResponseEntity.ok(userListResponse.users());
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateUserOnlineStatus(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusResponse userStatusResponse =
                this.userStatusService.updateByUserId(userId);

        return ResponseEntity.ok(userStatusResponse);
    }
}
