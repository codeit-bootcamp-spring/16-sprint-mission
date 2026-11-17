package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserListResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {
        UserResponse userResponse = userService.create(request, profile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @PatchMapping(value = "/{user-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("user-id") UUID userId,
            @Valid @RequestPart("request") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile) throws IOException {
        UserResponse userResponse = this.userService.update(userId, request, profile);

        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        this.userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers() {
        List<UserResponse> userList = this.userService.findAll();
        UserListResponse userListResponse = new UserListResponse(userList);

        return ResponseEntity.ok(userListResponse);
    }

    @PatchMapping("/{user-id}/status")
    public ResponseEntity<UserStatusResponse> updateUserOnlineStatus(
            @PathVariable("user-id") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserStatusResponse userStatusResponse = this.userStatusService.updateByUserId(userId);

        return ResponseEntity.ok(userStatusResponse);
    }
}
